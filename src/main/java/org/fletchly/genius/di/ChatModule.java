package org.fletchly.genius.di;

import dagger.Module;
import dagger.Provides;
import org.fletchly.genius.chat.ChatManager;
import org.fletchly.genius.chat.ChatManagerImpl;
import org.fletchly.genius.ollama.OllamaService;
import org.fletchly.genius.util.ConfigurationManager;

import javax.inject.Singleton;

@Module
public class ChatModule {
    @Provides
    @Singleton
    public ChatManager provideChatManager(ConfigurationManager configurationManager, OllamaService ollamaService) {
        return new ChatManagerImpl(configurationManager, ollamaService);
    }
}
