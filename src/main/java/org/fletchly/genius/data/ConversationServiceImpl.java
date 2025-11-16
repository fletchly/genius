package org.fletchly.genius.data;

import org.fletchly.genius.data.dao.ConversationDao;
import org.fletchly.genius.data.dao.MessageDao;
import org.fletchly.genius.data.dao.SqlConversationDao;
import org.fletchly.genius.data.dao.SqlMessageDao;
import org.fletchly.genius.data.model.Conversation;
import org.fletchly.genius.data.model.Message;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public class ConversationServiceImpl {
    private final Database db;
    private final ExecutorService executor;
    private final MessageDao messageDao;
    private final ConversationDao conversationDao;

    public ConversationServiceImpl(Database db, ExecutorService executor) {
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

    public CompletableFuture<Void> addMessage(UUID playerUuid, Message.Role role, String content) {
        return getConversationForPlayer(playerUuid).thenCompose(conversation ->
                messageDao.addMessage(conversation.id(), role.getRole(), content)
        );
    }

    public CompletableFuture<Void> addMessage(UUID playerUuid, Message message) {
        return getConversationForPlayer(playerUuid).thenCompose(conversation ->
                messageDao.addMessage(conversation.id(), message.role(), message.content())
        );
    }

    public CompletableFuture<Void> addUserMessage(UUID playerUuid, String content) {
        return getConversationForPlayer(playerUuid).thenCompose(conversation ->
                messageDao.addMessage(conversation.id(), Message.Role.USER.getRole(), content)
        );
    }

    public CompletableFuture<Void> addToolMessage(UUID playerUuid, String content) {
        return getConversationForPlayer(playerUuid).thenCompose(conversation ->
                messageDao.addMessage(conversation.id(), Message.Role.TOOL.getRole(), content)
        );
    }

    public CompletableFuture<Void> addAssistantMessage(UUID playerUuid, String content) {
        return getConversationForPlayer(playerUuid).thenCompose(conversation ->
                messageDao.addMessage(conversation.id(), Message.Role.ASSISTANT.getRole(), content)
        );
    }

    public CompletableFuture<Void> deleteAllMessagesForPlayer(UUID playerUuid) {
        return getConversationForPlayer(playerUuid).thenCompose(conversation ->
                messageDao.deleteMessages(conversation.id())
        );
    }
}
