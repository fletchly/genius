package org.fletchly.genius.di;

import dagger.Component;
import org.fletchly.genius.chat.ChatManager;
import org.fletchly.genius.command.AskCommand;

import javax.inject.Singleton;

@Singleton
@Component(modules = { PluginModule.class, OllamaModule.class, ChatModule.class, CommandModule.class, DataModule.class })
public interface PluginComponent {
    AskCommand askCommand();
}
