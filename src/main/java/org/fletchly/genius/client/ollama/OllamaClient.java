package org.fletchly.genius.client.ollama;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.Getter;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.fletchly.genius.client.AsyncHttpClient;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

@Builder
public class OllamaClient extends AsyncHttpClient {
    public static final MediaType JSON = MediaType.get("application/json");
    private static final ObjectMapper mapper = new ObjectMapper();

    private String baseUrl;
    private String apiKey;

    public CompletableFuture<OllamaResponse> generateChat(OllamaRequest ollamaRequest) {
        String url = baseUrl + "/api/chat";
        String authorization = "Bearer " + apiKey;

        byte[] jsonBody;
        try {
            jsonBody = mapper.writeValueAsBytes(ollamaRequest);
        } catch (JsonProcessingException e) {
            return CompletableFuture.failedFuture(
                    new OllamaParseException("Failed to serialize request body", e)
            );
        }

        // Create request
        RequestBody requestBody = RequestBody.create(jsonBody, JSON);
        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", authorization)
                .post(requestBody)
                .build();

        return executeAsync(request)
                .thenCompose(response -> {
                    try (response) {
                        String body = response.body().string();
                        OllamaResponse ollamaResponse = mapper.readValue(body, OllamaResponse.class);
                        return CompletableFuture.completedFuture(ollamaResponse);
                    } catch (JsonProcessingException ex) {
                        return CompletableFuture.failedFuture(
                                new OllamaParseException("Failed to deserialize response", ex)
                        );
                    } catch (IOException ex) {
                        return  CompletableFuture.failedFuture(
                                new OllamaClientException("Couldn't read HTTP response body", ex)
                        );
                    }
                })
                .exceptionally(ex -> {
                    // Unwrap CompletionException
                    Throwable cause = ex.getCause() != null ? ex.getCause() : ex;

                    if (cause instanceof HttpClientException httpEx) {
                        throw new OllamaHttpException(
                                "HTTP failure from Ollama API: " + httpEx.getMessage(),
                                httpEx,
                                httpEx.getStatusCode()
                        );
                    }

                    if (cause instanceof OllamaClientException) {
                        throw (OllamaClientException) cause;
                    }

                    if (cause instanceof IOException ioEx) {
                        throw new OllamaNetworkException("Network error communicating with Ollama", ioEx);
                    }

                    throw new OllamaClientException("Unexpected error in Ollama client", cause);
                });
    }

    public void closeClient() {
        super.closeClient();
    }

    public static class OllamaClientException extends RuntimeException {
        public OllamaClientException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class OllamaNetworkException extends OllamaClientException {
        public OllamaNetworkException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class OllamaHttpException extends OllamaClientException {
        @Getter
        private final int statusCode;

        public OllamaHttpException(String message, Throwable cause, int statusCode) {
            super(message, cause);
            this.statusCode = statusCode;
        }
    }

    public static class OllamaParseException extends OllamaClientException {
        public OllamaParseException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
