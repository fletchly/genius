package org.fletchly.genius.data.dao;

import org.fletchly.genius.data.model.Conversation;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface ConversationDao {
    CompletableFuture<Conversation> getOrCreateForPlayer(UUID playerUuid);

    CompletableFuture<Void> updateTimestamp(int conversationId);

    CompletableFuture<Void> deleteConversation(int conversationId);
}
