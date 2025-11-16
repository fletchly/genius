package org.fletchly.genius.di;

import dagger.Module;
import dagger.Provides;
import org.bukkit.plugin.java.JavaPlugin;
import org.fletchly.genius.chat.ChatManager;
import org.fletchly.genius.command.AskCommand;
import org.fletchly.genius.util.ConfigurationManager;

import javax.inject.Singleton;
import java.util.logging.Logger;

@Module
public class CommandModule {
    @Provides
    @Singleton
    AskCommand provideAskCommand(ConfigurationManager configurationManager, ChatManager chatManager, JavaPlugin plugin) {
        return new AskCommand(configurationManager, chatManager, plugin);
    }
}
