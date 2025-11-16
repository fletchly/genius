package org.fletchly.genius.chat;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface ChatManager {
    CompletableFuture<String> generateChat(UUID playerUuid, String prompt);
}
