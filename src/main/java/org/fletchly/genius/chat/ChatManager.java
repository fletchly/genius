package org.fletchly.genius.chat;

import org.fletchly.genius.Genius;
import org.fletchly.genius.data.ConversationService;
import org.fletchly.genius.data.Database;
import org.fletchly.genius.ollama.OllamaService;

import java.io.File;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class ChatManager {
    private final Genius genius;
    private final ConversationService conversationService;
    private final OllamaService ollamaService;

    public ChatManager() {
        genius = Genius.getInstance();
        conversationService = new ConversationService(genius.getDb(), genius.getDbExecutor());
        ollamaService = genius.getOllamaService(); // FIXME: You know what to do
    }
}
