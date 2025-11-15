package org.fletchly.genius.data.dao;

import org.fletchly.genius.data.model.Message;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface MessageDao {
    CompletableFuture<List<Message>> getMessagesForConversation(int conversationId);
    CompletableFuture<Void> addMessage(int conversationId, String role, String content);
    CompletableFuture<Void> deleteMessages(int conversationId);
}
