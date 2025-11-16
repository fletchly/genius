package org.fletchly.genius.chat;

import java.util.concurrent.CompletableFuture;

public interface ChatManager {
    CompletableFuture<String> generateChat(String prompt);
}
