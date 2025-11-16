package org.fletchly.genius.ollama.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.fletchly.genius.ollama.client.exceptions.OllamaClientException;
import org.fletchly.genius.ollama.client.exceptions.OllamaClientHttpException;
import org.fletchly.genius.ollama.client.exceptions.OllamaClientParseException;
import org.fletchly.genius.ollama.model.OllamaRequest;
import org.fletchly.genius.ollama.model.OllamaResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OllamaClientTest {

    private static final String BASE_URL = "http://localhost:11434";
    private static final String API_KEY = "test-api-key";

    @Mock
    private HttpClient mockHttpClient;

    @Mock
    private HttpResponse<String> mockHttpResponse;

    @Captor
    private ArgumentCaptor<HttpRequest> httpRequestCaptor;

    private OllamaClient ollamaClientWithKey;
    private OllamaClient ollamaClientWithoutKey;

    @BeforeEach
    void setUp() {
        ollamaClientWithKey = OllamaClientImpl.builder()
                .httpClient(mockHttpClient)
                .baseUrl(BASE_URL)
                .apiKey(API_KEY)
                .build();

        ollamaClientWithoutKey = OllamaClientImpl.builder()
                .httpClient(mockHttpClient)
                .baseUrl(BASE_URL)
                .apiKey(null)
                .build();
    }

    @Test
    void fetchResponse_Success_WithApiKey() throws ExecutionException, InterruptedException, JsonProcessingException {
        // Given
        OllamaRequest request = OllamaRequest.builder().model("test-model").build();
        String responseBody = "{\"model\":\"test-model\",\"created_at\":\"2023-08-04T19:22:45.499127Z\",\"message\":{\"role\":\"assistant\",\"content\":\"Hello!\"},\"done\":true}";

        // Use ObjectMapper to create the expected object, assuming OllamaResponse is a standard POJO.
        // This avoids making assumptions about its constructor.
        OllamaResponse expectedResponse = new ObjectMapper().readValue(responseBody, OllamaResponse.class);

        when(mockHttpClient.sendAsync(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(CompletableFuture.completedFuture(mockHttpResponse));
        when(mockHttpResponse.statusCode()).thenReturn(200);
        when(mockHttpResponse.body()).thenReturn(responseBody);

        // When
        CompletableFuture<OllamaResponse> futureResponse = ollamaClientWithKey.fetchResponse(request);
        OllamaResponse actualResponse = futureResponse.get();

        // Then
        verify(mockHttpClient).sendAsync(httpRequestCaptor.capture(), any());
        HttpRequest sentRequest = httpRequestCaptor.getValue();

        assertEquals("POST", sentRequest.method());
        assertEquals(BASE_URL + "/api/chat", sentRequest.uri().toString());
        assertTrue(sentRequest.headers().firstValue("Authorization").isPresent());
        assertEquals("Bearer " + API_KEY, sentRequest.headers().firstValue("Authorization").get());

        // Assuming OllamaResponse has a proper equals method (e.g., from Lombok @Data)
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void fetchResponse_Success_WithoutApiKey() throws ExecutionException, InterruptedException {
        // Given
        OllamaRequest request = OllamaRequest.builder().model("test-model").build();
        String responseBody = "{\"model\":\"test-model\",\"created_at\":\"2023-08-04T19:22:45.499127Z\",\"message\":{\"role\":\"assistant\",\"content\":\"Hello!\"},\"done\":true}";

        when(mockHttpClient.sendAsync(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(CompletableFuture.completedFuture(mockHttpResponse));
        when(mockHttpResponse.statusCode()).thenReturn(200);
        when(mockHttpResponse.body()).thenReturn(responseBody);

        // When
        CompletableFuture<OllamaResponse> futureResponse = ollamaClientWithoutKey.fetchResponse(request);
        futureResponse.get(); // Wait for completion

        // Then
        verify(mockHttpClient).sendAsync(httpRequestCaptor.capture(), any());
        HttpRequest sentRequest = httpRequestCaptor.getValue();

        assertTrue(sentRequest.headers().firstValue("Authorization").isPresent());
        assertEquals("Bearer NONE", sentRequest.headers().firstValue("Authorization").get());
    }

    @Test
    void fetchResponse_InvalidUrl_Fails() {
        // Given
        OllamaClientImpl clientWithInvalidUrl = OllamaClientImpl.builder()
                .httpClient(mockHttpClient)
                .baseUrl("this is not a valid url")
                .build();
        OllamaRequest request = OllamaRequest.builder().model("test-model").build();

        // When
        CompletableFuture<OllamaResponse> futureResponse = clientWithInvalidUrl.fetchResponse(request);

        // Then
        ExecutionException exception = assertThrows(ExecutionException.class, futureResponse::get);
        Throwable cause = exception.getCause();
        assertInstanceOf(OllamaClientParseException.class, cause);
        assertEquals("Invalid URL provided", cause.getMessage());
    }

    @Test
    void fetchResponse_HttpError_Fails() {
        // Given
        OllamaRequest request = OllamaRequest.builder().model("test-model").build();
        when(mockHttpClient.sendAsync(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(CompletableFuture.completedFuture(mockHttpResponse));
        when(mockHttpResponse.statusCode()).thenReturn(500);

        // When
        CompletableFuture<OllamaResponse> futureResponse = ollamaClientWithKey.fetchResponse(request);

        // Then
        ExecutionException exception = assertThrows(ExecutionException.class, futureResponse::get);
        Throwable cause = exception.getCause();
        assertInstanceOf(OllamaClientHttpException.class, cause);
        assertEquals("Ollama API responded with an unexpected status code", cause.getMessage());
        assertEquals(500, ((OllamaClientHttpException) cause).getStatusCode());
    }

    @Test
    void fetchResponse_ResponseParsingError_Fails() {
        // Given
        OllamaRequest request = OllamaRequest.builder().model("test-model").build();
        String invalidResponseBody = "{\"model\":\"test-model\",,}"; // Invalid JSON

        when(mockHttpClient.sendAsync(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(CompletableFuture.completedFuture(mockHttpResponse));
        when(mockHttpResponse.statusCode()).thenReturn(200);
        when(mockHttpResponse.body()).thenReturn(invalidResponseBody);

        // When
        CompletableFuture<OllamaResponse> futureResponse = ollamaClientWithKey.fetchResponse(request);

        // Then
        ExecutionException exception = assertThrows(ExecutionException.class, futureResponse::get);
        Throwable cause = exception.getCause();
        assertInstanceOf(OllamaClientParseException.class, cause);
        assertEquals("Failed to deserialize response", cause.getMessage());
    }

    @Test
    void fetchResponse_NetworkError_Fails() {
        // Given
        OllamaRequest request = OllamaRequest.builder().model("test-model").build();
        RuntimeException networkException = new RuntimeException("Network is down");
        when(mockHttpClient.sendAsync(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(CompletableFuture.failedFuture(networkException));

        // When
        CompletableFuture<OllamaResponse> futureResponse = ollamaClientWithKey.fetchResponse(request);

        // Then
        ExecutionException exception = assertThrows(ExecutionException.class, futureResponse::get);
        Throwable cause = exception.getCause();
        assertInstanceOf(OllamaClientException.class, cause);
        assertEquals("Ollama client failed", cause.getMessage());
        assertEquals(networkException, cause.getCause());
    }
}
