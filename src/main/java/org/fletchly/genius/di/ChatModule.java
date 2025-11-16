package org.fletchly.genius.di;

import dagger.Module;
import dagger.Provides;
import org.fletchly.genius.chat.ChatManager;
import org.fletchly.genius.chat.ChatManagerImpl;
import org.fletchly.genius.ollama.OllamaService;

import javax.inject.Singleton;
import java.util.logging.Logger;

@Module
public class ChatModule {
    @Provides
    @Singleton
    public ChatManager provideChatManager(OllamaService ollamaService, Logger logger) {
        return new ChatManagerImpl(ollamaService, logger);
    }
}
