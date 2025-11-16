package org.fletchly.genius.chat;

import org.fletchly.genius.Genius;
import org.fletchly.genius.data.ConversationService;
import org.fletchly.genius._ollama.OllamaService;

public class ChatManager {
    private final Genius genius;
    private final ConversationService conversationService;
    private final OllamaService ollamaService;

    public ChatManager() {
        genius = Genius.getInstance();
        conversationService = new ConversationService(genius.getDb(), genius.getDbExecutor());
        ollamaService = genius.getOllamaService(); // FIXME: You know what to do
    }

//    public CompletableFuture<String> generateChat(UUID playerUuid, String prompt) {
//        return CompletableFuture.supplyAsync(() -> {
//            conversationService.getConversationForPlayer(playerUuid).thenCompose(conversation ->
//            {
//
//            })
//        })
//    }
}
