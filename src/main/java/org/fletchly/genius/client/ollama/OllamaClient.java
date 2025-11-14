package org.fletchly.genius.client.ollama;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.Getter;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.fletchly.genius.client.AsyncHttpClient;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

@Builder
public class OllamaClient extends AsyncHttpClient {
    public static final MediaType JSON = MediaType.get("application/json");
    private static final ObjectMapper mapper = new ObjectMapper();

    private String baseUrl;
    private String apiKey;

    /**
     * Sends a chat request to the Ollama API and processes the response asynchronously.
     *
     * @param ollamaRequest the request object containing the model, messages, and options
     *                      to be sent to the Ollama API; must not be null.
     * @return a CompletableFuture containing the parsed response as an {@code OllamaResponse} object
     *         or an exception if the request or response processing fails.
     */
    public CompletableFuture<OllamaResponse> generateChat(OllamaRequest ollamaRequest) {
        // Initialize base URL and Authorization header values
        String url = baseUrl + "/api/chat";
        String authorization = "Bearer " + apiKey;

        // Attempt to serialize request to JSON
        byte[] jsonBody;
        try {
            jsonBody = mapper.writeValueAsBytes(ollamaRequest);
        } catch (JsonProcessingException e) {
            return CompletableFuture.failedFuture(e);
        }

        // Create request
        RequestBody requestBody = RequestBody.create(jsonBody, JSON);

        Request request = new Request.Builder().url(url).header("Authorization", authorization).post(requestBody).build();

        // Execute request
        return executeAsync(request).thenApply(response -> {
            try (response) {
                // Throw exception if request fails
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected HTTP code " + response.code() + ": " + response.body().string());
                }
                // Map response to Ollama Response
                return mapper.readValue(response.body().string(), OllamaResponse.class);
            } catch (Exception e) {
                throw new RuntimeException("Failed to parse Ollama response", e);
            }
        }).exceptionally(throwable -> {
            throw new RuntimeException("Request to Ollama failed", throwable);
        });
    }

    public void closeClient() {
        super.closeClient();
    }
}
