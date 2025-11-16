package org.fletchly.genius.di;

import dagger.Module;
import dagger.Provides;
import org.fletchly.genius.chat.ChatManager;
import org.fletchly.genius.chat.ChatManagerImpl;
import org.fletchly.genius.context.ContextService;
import org.fletchly.genius.ollama.OllamaService;
import org.fletchly.genius.util.ConfigurationManager;

import javax.inject.Singleton;
import java.util.logging.Logger;

@Module
public class ChatModule {
    @Provides
    @Singleton
    public ChatManager provideChatManager(OllamaService ollamaService, ContextService contextService, Logger logger) {
        return new ChatManagerImpl(ollamaService, contextService, logger);
    }
}
