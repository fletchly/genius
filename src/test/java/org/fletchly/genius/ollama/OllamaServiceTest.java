package org.fletchly.genius.ollama;

import org.fletchly.genius.common.Message;
import org.fletchly.genius.ollama.client.OllamaClientImpl;
import org.fletchly.genius.ollama.model.OllamaMessage;
import org.fletchly.genius.ollama.model.OllamaOptions;
import org.fletchly.genius.ollama.model.OllamaRequest;
import org.fletchly.genius.ollama.model.OllamaResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link OllamaService}.
 */
class OllamaServiceTest {

    @Mock
    private OllamaClientImpl mockOllamaClient;

    @Captor
    private ArgumentCaptor<OllamaRequest> ollamaRequestCaptor;

    private OllamaService ollamaService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Tests that {@link OllamaService#generateChat(Message...)} builds the correct request
     * and returns the expected message from the response.
     */
    @Test
    void generateChat_shouldBuildCorrectRequestAndReturnMessage() throws ExecutionException, InterruptedException {
        // Given
        String model = "test-model";
        String systemPrompt = "You are a helpful assistant.";
        OllamaMessage systemPromptMessage = OllamaMessage.builder().role("system").content(systemPrompt).build();
        OllamaOptions options = OllamaOptions.builder()
                .temperature(0.7)
                .topK(50)
                .topP(0.9)
                .numPredict(1024)
                .build();

        ollamaService = new OllamaServiceImpl(mockOllamaClient, systemPromptMessage, options, model);

        Message userMessage = OllamaMessage.builder().role("user").content("Hello, who are you?").build();

        OllamaMessage assistantOllamaResponse = OllamaMessage.builder().role(Message.Role.ASSISTANT.toString()).content("I am a helpful assistant.").build();
        OllamaResponse ollamaResponse = OllamaResponse.builder()
                .message(assistantOllamaResponse)
                .build();

        when(mockOllamaClient.fetchResponse(any(OllamaRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(ollamaResponse));

        // When
        CompletableFuture<Message> futureResult = ollamaService.generateChat(userMessage);
        Message result = futureResult.get();

        // Then
        verify(mockOllamaClient).fetchResponse(ollamaRequestCaptor.capture());
        OllamaRequest capturedRequest = ollamaRequestCaptor.getValue();

        assertEquals(model, capturedRequest.model());

        List<OllamaMessage> expectedMessages = new ArrayList<>();
        expectedMessages.add(systemPromptMessage);
        expectedMessages.add(OllamaMessage.builder().role(userMessage.role()).content(userMessage.content()).build());
        
        assertEquals(expectedMessages, capturedRequest.messages());

        assertEquals(options, capturedRequest.options());
        assertEquals(false, capturedRequest.stream());
        assertEquals(false, capturedRequest.think());

        assertEquals(assistantOllamaResponse, result);
    }

    /**
     * Tests that {@link OllamaServiceImpl.OllamaServiceBuilder} correctly builds an {@link OllamaServiceImpl}
     * instance with the specified configuration.
     */
    @Test
    void ollamaServiceBuilder_shouldBuildServiceWithCorrectConfiguration() {
        // Given
        String model = "test-model";
        String systemPrompt = "You are a helpful assistant.";
        String baseUrl = "http://localhost:11434";
        String apiKey = "test-api-key";
        double temperature = 0.8;
        int topK = 40;
        double topP = 0.8;
        int numPredict = 2048;

        // When
        OllamaServiceImpl service = OllamaServiceImpl.builder()
                .model(model)
                .systemPrompt(systemPrompt)
                .baseUrl(baseUrl)
                .apiKey(apiKey)
                .temperature(temperature)
                .topK(topK)
                .topP(topP)
                .numPredict(numPredict)
                .build();

        // Then
        assertEquals(model, service.getModel());
        assertEquals(systemPrompt, service.getSystemPromptMessage().content());
        assertEquals(Message.Role.SYSTEM.toString(), service.getSystemPromptMessage().role());

        OllamaOptions options = service.getOptions();
        assertEquals(temperature, options.temperature());
        assertEquals(topK, options.topK());
        assertEquals(topP, options.topP());
        assertEquals(numPredict, options.numPredict());

        assertNotNull(service.getClient());
    }
}
