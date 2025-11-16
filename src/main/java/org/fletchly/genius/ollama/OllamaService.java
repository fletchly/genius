package org.fletchly.genius.ollama;

import org.fletchly.genius.common.Message;

import java.util.concurrent.CompletableFuture;

public interface OllamaService {
    CompletableFuture<Message> generateChat(Message... messages);
}
