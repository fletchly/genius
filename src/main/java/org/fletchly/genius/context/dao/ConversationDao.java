package org.fletchly.genius.context.dao;

import org.fletchly.genius.context.model.ContextConversation;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface ConversationDao {
    CompletableFuture<Optional<ContextConversation>> getByPlayerUuid(UUID playerUuid);
    CompletableFuture<Void> upsert(ContextConversation conversation);
    CompletableFuture<Void> deleteByPlayerUuid(UUID playerUuid);
}
