package org.fletchly.genius.ollama.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.fletchly.genius.ollama.client.exceptions.OllamaClientException;
import org.fletchly.genius.ollama.client.exceptions.OllamaClientHttpException;
import org.fletchly.genius.ollama.client.exceptions.OllamaClientParseException;
import org.fletchly.genius.ollama.model.OllamaRequest;
import org.fletchly.genius.ollama.model.OllamaResponse;
import org.fletchly.genius.util.ConfigurationManager;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

/**
 * Http client for communicating with the Ollama API
 */
public class OllamaClientImpl implements OllamaClient {
    private static final int CONNECT_TIMEOUT_S = 30; // Connection timeout (in S)
    private static final String JSON = "application/json"; // JSON content type
    private static final int HTTP_OK = 200; // HTTP OK response code
    private static final ObjectMapper mapper = new ObjectMapper();

    private final ConfigurationManager configurationManager;
    private final HttpClient httpClient;

    @Inject
    public OllamaClientImpl(ConfigurationManager configurationManager, HttpClient httpClient) {
        this.configurationManager = configurationManager;
        this.httpClient = httpClient;
    }

    /**
     * Sends an asynchronous HTTP request to the Ollama chat API with the provided request payload,
     * then processes the response to return an {@code OllamaResponse}. Handles URL construction,
     * request serialization, status validation, and response parsing.
     *
     * @param request the {@code OllamaRequest} containing the model, messages, options, and flags
     *                for the API interaction.
     * @return a {@code CompletableFuture<OllamaResponse>} representing the result of the
     * asynchronous operation. The future completes with the deserialized {@code OllamaResponse}
     * if the operation is successful, or with an exception if any errors occur.
     */
    public CompletableFuture<OllamaResponse> fetchResponse(OllamaRequest request) {
        String baseUrl = configurationManager.ollamaBaseUrl();
        String apiKey = configurationManager.ollamaApiKey();

        // Attempt to parse URL
        URI uri;
        try {
            uri = URI.create(baseUrl).resolve("/api/chat");
        } catch (Exception ex) {
            return CompletableFuture.failedFuture(
                    new OllamaClientParseException("Invalid URL provided", ex)
            );
        }

        // Attempt to parse request to JSON byte array
        byte[] rawBody;
        try {
            rawBody = mapper.writeValueAsBytes(request);
        } catch (JsonProcessingException ex) {
            return CompletableFuture.failedFuture(
                    new OllamaClientParseException("Failed to serialize request body", ex)
            );
        }

        // Construct request body
        BodyPublisher requestBody = HttpRequest.BodyPublishers.ofByteArray(rawBody);

        // Set authorization
        // Token is set to NONE when none is provided.
        // Handles self-hosted (no authentication needed) scenarios
        String authorization = "Bearer " + apiKey;

        // Construct request
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .timeout(Duration.ofSeconds(CONNECT_TIMEOUT_S))
                .uri(uri)
                .header("Authorization", authorization)
                .header("Content-Type", JSON)
                .header("Accept", JSON)
                .POST(requestBody)
                .build();

        // Send request
        return httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString())
                .thenCompose(this::checkStatus)
                .thenCompose(this::parseBody)
                .exceptionallyCompose(this::handleExceptions);
    }

    /**
     * Validates the HTTP response status code and returns the response body if the status is successful.
     * If the status code is not 200 (HTTP OK), the method returns a failed future with an
     * {@code OllamaClientHttpException}.
     *
     * @param httpResponse the HTTP response containing the status code and body of the request.
     * @return a {@code CompletableFuture<String>} that completes with the response body if the status
     * code is 200, or completes exceptionally with an {@code OllamaClientHttpException} if the
     * status code indicates an error.
     */
    private @NotNull CompletableFuture<String> checkStatus(HttpResponse<String> httpResponse) {
        // Check for 200 OK response
        return httpResponse.statusCode() == HTTP_OK
                // Return response body on success
                ? CompletableFuture.completedFuture(httpResponse.body())
                // Return failed future on exception
                : CompletableFuture.failedFuture(
                new OllamaClientHttpException(
                        "Ollama API responded with an unexpected status code",
                        null,
                        httpResponse.statusCode()
                )
        );
    }

    /**
     * Parses the provided JSON string into an {@code OllamaResponse} object asynchronously.
     * If the parsing fails, a failed {@code CompletableFuture} with an {@code OllamaClientParseException}
     * is returned.
     *
     * @param body the JSON string representation of the response body to be parsed into
     *             an {@code OllamaResponse} object.
     * @return a {@code CompletableFuture<OllamaResponse>} object that completes successfully
     * with the parsed {@code OllamaResponse} if the input is valid JSON, or completes
     * exceptionally with an {@code OllamaClientParseException} in case of a parsing error.
     */
    private @NotNull CompletableFuture<OllamaResponse> parseBody(String body) {
        try {
            // Attempt to parse response body to an OllamaResponse
            OllamaResponse response = mapper.readValue(body, OllamaResponse.class);
            return CompletableFuture.completedFuture(response);
        } catch (JsonProcessingException ex) {
            // Return failed future on exception
            return CompletableFuture.failedFuture(
                    new OllamaClientParseException("Failed to deserialize response", ex)
            );
        }
    }

    /**
     * Handles exceptions occurring during asynchronous processing by analyzing, unwrapping, and appropriately
     * propagating or wrapping them into a domain-specific exception.
     *
     * @param throwable the exception or error that occurred during processing.
     * @return a {@code CompletableFuture<OllamaResponse>} that completes exceptionally. If the {@code throwable}
     * is an {@code OllamaClientException}, it is propagated as is. For other exceptions, an
     * {@code OllamaClientException} is created and returned.
     */
    private @NotNull CompletableFuture<OllamaResponse> handleExceptions(Throwable throwable) {
        // Unwrap exception
        Throwable cause = throwable.getCause() != null
                ? throwable.getCause()
                : throwable;

        // Propagate existing
        if (cause instanceof OllamaClientException) {
            return CompletableFuture.failedFuture(cause);
        }

        // Wrap HttpClient exceptions (IOException, etc.) and any others
        return CompletableFuture.failedFuture(
                new OllamaClientException("Ollama client failed", cause)
        );
    }
}
