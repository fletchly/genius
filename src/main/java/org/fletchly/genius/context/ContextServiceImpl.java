package org.fletchly.genius.context;

import org.fletchly.genius.common.Message;
import org.fletchly.genius.context.dao.ConversationDao;
import org.fletchly.genius.context.dao.MessageDao;
import org.fletchly.genius.context.model.ContextConversation;
import org.fletchly.genius.context.model.ContextMessage;
import org.fletchly.genius.util.ConfigurationManager;

import javax.inject.Inject;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class ContextServiceImpl implements ContextService {

    private final MessageDao messageDao;
    private final ConversationDao conversationDao;
    private final ConfigurationManager configurationManager;

    @Inject
    public ContextServiceImpl(MessageDao messageDao, ConversationDao conversationDao, ConfigurationManager configurationManager) {
        this.messageDao = messageDao;
        this.conversationDao = conversationDao;
        this.configurationManager = configurationManager;
    }

    @Override
    public CompletableFuture<ContextConversation> getOrCreateConversationForPlayer(UUID playerUuid) {
        return conversationDao.getOrInsertByPlayerUuid(playerUuid).thenComposeAsync(conversation -> {
            if (conversation.isPresent()) {
                messageDao.findByConversationId(conversation.get().id()).thenAccept(messages -> {
                    conversation.get().messages().addAll(messages);
                });
                return CompletableFuture.completedFuture(conversation.get());
            }
            return CompletableFuture.completedFuture(ContextConversation.builder()
                    .playerUuid(playerUuid)
                    .messages(List.of())
                    .build());
        });
    }

    @Override
    public CompletableFuture<Void> addMessageForPlayer(UUID playerUuid, Message message) {
        return getOrCreateConversationForPlayer(playerUuid).thenComposeAsync(conversation -> {
            if (conversation.messages().size() > configurationManager.contextMaxPlayerMessages()) {
                messageDao.deleteById(conversation.messages().getFirst().id());
            }

            return messageDao.insert(ContextMessage.builder()
                    .conversationId(conversation.id())
                    .role(message.role())
                    .content(message.content())
                    .build()
            );
        });
    }

    @Override
    public CompletableFuture<Void> deleteConversationForPlayer(UUID playerUuid) {
        return conversationDao.deleteByPlayerUuid(playerUuid);
    }
}
