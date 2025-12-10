package io.fletchly.genius

import dagger.Component
import io.fletchly.genius.command.AskCommand
import io.fletchly.genius.command.CommandModule
import io.fletchly.genius.command.ManageCommand
import io.fletchly.genius.config.ConfigModule
import io.fletchly.genius.context.ContextModule
import io.fletchly.genius.conversation.ConversationModule
import io.fletchly.genius.ollama.OllamaModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        PluginModule::class,
        ConfigModule::class,
        ContextModule::class,
        CommandModule::class,
        ConversationModule::class,
        OllamaModule::class
    ]
)
interface PluginComponent {
    fun askCommand(): AskCommand

    fun manageCommand(): ManageCommand
}