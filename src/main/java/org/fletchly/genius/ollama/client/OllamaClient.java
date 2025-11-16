package org.fletchly.genius.ollama.client;

import org.fletchly.genius.ollama.model.OllamaRequest;
import org.fletchly.genius.ollama.model.OllamaResponse;

import java.util.concurrent.CompletableFuture;

public interface OllamaClient {
    CompletableFuture<OllamaResponse> fetchResponse(OllamaRequest request);
}
