package org.fletchly.genius.data;

import org.fletchly.genius.data.model.Conversation;
import org.fletchly.genius.data.model.Message;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface ConversationService {
    CompletableFuture<Conversation> getConversationForPlayer(UUID playerUuid);

    CompletableFuture<Void> addMessage(UUID playerUuid, Message.Role role, String content);

    CompletableFuture<Void> addMessage(UUID playerUuid, Message message);

    CompletableFuture<Void> addUserMessage(UUID playerUuid, String content);

    CompletableFuture<Void> addToolMessage(UUID playerUuid, String content);

    CompletableFuture<Void> addAssistantMessage(UUID playerUuid, String content);

    CompletableFuture<Void> deleteAllMessages(UUID playerUuid);
}
