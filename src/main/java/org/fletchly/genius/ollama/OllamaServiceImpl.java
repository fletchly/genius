package org.fletchly.genius.ollama;

import org.fletchly.genius.common.Message;
import org.fletchly.genius.ollama.client.OllamaClient;
import org.fletchly.genius.ollama.model.OllamaMessage;
import org.fletchly.genius.ollama.model.OllamaOptions;
import org.fletchly.genius.ollama.model.OllamaRequest;
import org.fletchly.genius.ollama.model.OllamaResponse;
import org.fletchly.genius.util.ConfigurationManager;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * The OllamaService class is responsible for interfacing with the Ollama API. It facilitates
 * building requests and processing responses to generate chat messages through the Ollama API.
 */
public class OllamaServiceImpl implements OllamaService {
    private static final boolean DISABLE = false;

    private final OllamaMessage systemPromptMessage;
    private final OllamaOptions ollamaOptions;

    private final ConfigurationManager configurationManager;
    private final OllamaClient ollamaClient;

    @Inject
    public OllamaServiceImpl(ConfigurationManager configurationManager, OllamaClient ollamaClient) {
        this.configurationManager = configurationManager;
        this.ollamaClient = ollamaClient;

        systemPromptMessage = OllamaMessage.builder()
                .role(Message.Roles.SYSTEM)
                .content(configurationManager.geniusSystemPrompt())
                .build();

        ollamaOptions = OllamaOptions.builder()
                .temperature(configurationManager.ollamaTemperature())
                .topK(configurationManager.ollamaTopK())
                .topP(configurationManager.ollamaTopP())
                .numPredict(configurationManager.ollamaNumPredict())
                .build();
    }

    public CompletableFuture<Message> generateChat(Message... messages) {
        // Map Messages to OllamaMessages
        List<OllamaMessage> ollamaMessages = Arrays.stream(messages)
                .map(this::mapMessage)
                .toList();

        // Build ollama request
        // Explicitly disable thinking and streaming
        // Supporting them is out of scope
        var ollamaRequest = OllamaRequest.builder()
                .model(configurationManager.ollamaModel())
                .message(systemPromptMessage)
                .messages(ollamaMessages)
                .options(ollamaOptions)
                .stream(DISABLE)
                .think(DISABLE)
                .build();

        return ollamaClient.fetchResponse(ollamaRequest)
                .thenApply(OllamaResponse::message);
    }

    private OllamaMessage mapMessage(Message message) {
        return OllamaMessage.builder()
                .role(message.role())
                .content(message.content())
                .build();
    }
}