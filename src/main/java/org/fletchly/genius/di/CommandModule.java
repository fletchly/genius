package org.fletchly.genius.di;

import dagger.Module;
import dagger.Provides;
import org.fletchly.genius.chat.ChatManager;
import org.fletchly.genius.command.AskCommand;
import org.fletchly.genius.util.ConfigurationManager;

import javax.inject.Singleton;

@Module
public class CommandModule {
    @Provides
    @Singleton
    AskCommand provideAskCommand(ConfigurationManager configurationManager, ChatManager chatManager) {
        return new AskCommand(configurationManager, chatManager);
    }
}
