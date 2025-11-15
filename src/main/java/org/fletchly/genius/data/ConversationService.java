package org.fletchly.genius.data;

import lombok.Getter;
import org.fletchly.genius.data.dao.ConversationDao;
import org.fletchly.genius.data.dao.MessageDao;
import org.fletchly.genius.data.dao.SqlConversationDao;
import org.fletchly.genius.data.dao.SqlMessageDao;
import org.fletchly.genius.data.model.Conversation;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public class ConversationService {
    private final DatabaseManager db;
    private final ExecutorService executor;
    private final MessageDao messageDao;
    private final ConversationDao conversationDao;

    public ConversationService(DatabaseManager db, ExecutorService executor) {
        this.db = db;
        this.executor = executor;

        messageDao = new SqlMessageDao(this.db, this.executor);
        conversationDao = new SqlConversationDao(this.db, this.executor);
    }

    public CompletableFuture<Conversation> getConversationForPlayer(UUID playerUuid) {
        return conversationDao.getOrCreateForPlayer(playerUuid)
                .thenCompose(conversation ->
                        messageDao.getMessagesForConversation(conversation.id())
                                .thenApply(messages -> Conversation.builder()
                                        .id(conversation.id())
                                        .playerUuid(conversation.playerUuid())
                                        .created(conversation.created())
                                        .updated(conversation.updated())
                                        .messages(messages)
                                        .build()
                                )
                );
    }

    public CompletableFuture<Void> addMessage(UUID playerUuid, Role role, String content) {
        return getConversationForPlayer(playerUuid).thenCompose(conversation ->
                messageDao.addMessage(conversation.id(), role.getRole(), content)
        );
    }

    public CompletableFuture<Void> addUserMessage(UUID playerUuid, String content) {
        return getConversationForPlayer(playerUuid).thenCompose(conversation ->
                messageDao.addMessage(conversation.id(), Role.USER.getRole(), content)
        );
    }

    public CompletableFuture<Void> addToolMessage(UUID playerUuid, String content) {
        return getConversationForPlayer(playerUuid).thenCompose(conversation ->
                messageDao.addMessage(conversation.id(), Role.TOOL.getRole(), content)
        );
    }

    public CompletableFuture<Void> addAssistantMessage(UUID playerUuid, String content) {
        return getConversationForPlayer(playerUuid).thenCompose(conversation ->
                messageDao.addMessage(conversation.id(), Role.ASSISTANT.getRole(), content)
        );
    }

    public CompletableFuture<Void> deleteMessages(UUID playerUuid) {
        return getConversationForPlayer(playerUuid).thenCompose(conversation ->
                messageDao.deleteMessages(conversation.id())
        );
    }

    public enum Role {
        USER("user"),
        TOOL("tool"),
        ASSISTANT("assistant");

        @Getter
        private final String role;

        Role(String role) {
            this.role = role;
        }
    }
}
