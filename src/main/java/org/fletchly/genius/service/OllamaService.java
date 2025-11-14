package org.fletchly.genius.service;

import org.fletchly.genius.client.ollama.OllamaClient;
import org.fletchly.genius.client.ollama.OllamaMessage;
import org.fletchly.genius.client.ollama.OllamaOptions;
import org.fletchly.genius.client.ollama.OllamaRequest;


public class OllamaService {
    private final OllamaClient client;
    private final String model;
    private final OllamaMessage systemPrompt;
    private final OllamaOptions options;

    public OllamaService(String model, OllamaMessage systemPrompt, String baseUrl, String apiKey, double temperature, int topK, double topP, int numPredict) {
        this.client = OllamaClient.builder()
                .baseUrl(baseUrl)
                .apiKey(apiKey)
                .build();

        this.model = model;

        this.systemPrompt = systemPrompt;

        this.options = OllamaOptions.builder()
                .temperature(temperature)
                .topK(topK)
                .topP(topP)
                .numPredict(numPredict)
                .build();
    }

    public String generateChat(String prompt) {
        OllamaMessage userPrompt = OllamaMessage.builder()
                .role("user")
                .content(prompt)
                .build();

        OllamaRequest chatRequest = OllamaRequest.builder()
                .model(model)
                .message(systemPrompt)
                .message(userPrompt)
                .options(options)
                .build();

        var response = client.generateChat(chatRequest).join();

        return response.getMessage().getContent();
    }

    public void close() {
        client.closeClient();
    }

    public static OllamaServiceBuilder builder() {
        return new OllamaServiceBuilder();
    }

    public static class OllamaServiceBuilder {
        private String model;
        private String systemPrompt;
        private String baseUrl;
        private String apiKey;
        private double temperature;
        private int topK;
        private double topP;
        private int numPredict;

        public OllamaServiceBuilder() {
        }

        public OllamaServiceBuilder model(String model) {
            this.model = model;
            return this;
        }

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
            OllamaMessage systemPromptMessage = OllamaMessage.builder()
                    .role("system")
                    .content(systemPrompt)
                    .build();

            return new OllamaService(model, systemPromptMessage, baseUrl, apiKey, temperature, topK, topP, numPredict);
        }
    }
}
