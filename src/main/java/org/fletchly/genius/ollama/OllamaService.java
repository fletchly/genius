package org.fletchly.genius.ollama;

import lombok.Builder;
import lombok.Getter;
import lombok.Value;
import org.fletchly.genius.common.Message;
import org.fletchly.genius.ollama.client.OllamaClient;
import org.fletchly.genius.ollama.model.OllamaMessage;
import org.fletchly.genius.ollama.model.OllamaOptions;
import org.fletchly.genius.ollama.model.OllamaRequest;
import org.fletchly.genius.ollama.model.OllamaResponse;

import java.net.http.HttpClient;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * The OllamaService class is responsible for interfacing with the Ollama API. It facilitates
 * building requests and processing responses to generate chat messages through the Ollama API.
 */
@Value
@Builder(builderClassName = "OllamaServiceBuilder")
public class OllamaService {
    private static final boolean DISABLE = false;

    @Getter
    OllamaClient client;

    @Getter
    OllamaMessage systemPromptMessage;

    @Getter
    OllamaOptions options;

    @Getter
    String model;

    public CompletableFuture<Message> generateChat(Message... messages) {
        // Map Messages to OllamaMessages
        List<OllamaMessage> ollamaMessages = Arrays.stream(messages)
                .map(this::mapMessage)
                .toList();

        // Build ollama request
        // Explicitly disable thinking and streaming
        // Supporting them is out of scope
        var ollamaRequest = OllamaRequest.builder()
                .model(model)
                .message(systemPromptMessage)
                .messages(ollamaMessages)
                .options(options)
                .stream(DISABLE)
                .think(DISABLE)
                .build();

        return client.fetchResponse(ollamaRequest)
                .thenApply(OllamaResponse::message);
    }

    private OllamaMessage mapMessage (Message message) {
        return OllamaMessage.builder()
                .role(message.role())
                .content(message.content())
                .build();
    }

    public static class OllamaServiceBuilder {
        private String systemPrompt;
        private String baseUrl;
        private String apiKey;
        private double temperature;
        private int topK;
        private double topP;
        private int numPredict;

        public OllamaServiceBuilder systemPrompt(String systemPrompt) {
            this.systemPrompt = systemPrompt;
            return this;
        }

        public OllamaServiceBuilder baseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        public OllamaServiceBuilder apiKey(String apiKey) {
            this.apiKey = apiKey;
            return this;
        }

        public OllamaServiceBuilder temperature(double temperature) {
            this.temperature = temperature;
            return this;
        }

        public OllamaServiceBuilder topK(int topK) {
            this.topK = topK;
            return this;
        }

        public OllamaServiceBuilder topP(double topP) {
            this.topP = topP;
            return this;
        }

        public OllamaServiceBuilder numPredict(int numPredict) {
            this.numPredict = numPredict;
            return this;
        }

        public OllamaService build() {
            OllamaClient ollamaClient = OllamaClient.builder()
                    .httpClient(HttpClient.newHttpClient())
                    .baseUrl(baseUrl)
                    .apiKey(apiKey)
                    .build();

            OllamaMessage systemPromptMessage = OllamaMessage.builder()
                    .role(Message.Role.SYSTEM.toString())
                    .content(systemPrompt)
                    .build();

            OllamaOptions ollamaOptions = OllamaOptions.builder()
                    .temperature(temperature)
                    .topK(topK)
                    .topP(topP)
                    .numPredict(numPredict)
                    .build();

            // Set the fields that Lombok's builder expects
            this.client = ollamaClient;
            this.systemPromptMessage = systemPromptMessage;
            this.options = ollamaOptions;

            return new OllamaService(ollamaClient, systemPromptMessage, ollamaOptions, model);
        }
    }
}