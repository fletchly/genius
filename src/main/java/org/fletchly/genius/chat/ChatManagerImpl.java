package org.fletchly.genius.chat;

import org.fletchly.genius.ollama.OllamaService;
import org.fletchly.genius.util.ConfigurationManager;

import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;

public class ChatManagerImpl implements ChatManager {
    private final ConfigurationManager configurationManager;
    private final OllamaService ollamaService;

    @Inject
    public ChatManagerImpl(ConfigurationManager configurationManager, OllamaService ollamaService) {
        this.ollamaService = ollamaService;
        this.configurationManager = configurationManager;
    }

    @Override
    public CompletableFuture<String> generateChat(String prompt) {
        return null;
    }
}
