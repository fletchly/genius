package org.fletchly.genius.context;

import org.fletchly.genius.common.Message;
import org.fletchly.genius.context.model.ContextConversation;
import org.fletchly.genius.context.model.ContextMessage;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface ContextService {
    public CompletableFuture<ContextConversation> getOrCreateConversationForPlayer(UUID playerUuid);
    public CompletableFuture<Void> addMessageForPlayer(UUID playerUuid, Message message);
    public CompletableFuture<Void> deleteConversationForPlayer(UUID playerUuid);
}
