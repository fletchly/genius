package org.fletchly.genius.context.dao;

import org.fletchly.genius.context.model.ContextMessage;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface MessageDao {
    CompletableFuture<Optional<List<ContextMessage>>> findByConversationId(long conversationId);
    CompletableFuture<Void> insert(ContextMessage message);
    CompletableFuture<Void> deleteById(long id);
}
