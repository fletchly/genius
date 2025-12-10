package io.fletchly.genius.command

import dagger.Module
import dagger.Provides
import io.fletchly.genius.Genius
import io.fletchly.genius.config.ConfigManager
import io.fletchly.genius.conversation.service.ConversationManager
import kotlinx.coroutines.CoroutineScope
import java.util.logging.Logger
import javax.inject.Singleton

@Module
class CommandModule {
    @Provides
    @Singleton
    fun provideAskCommand(
        configManager: ConfigManager,
        plugin: Genius,
        pluginScope: CoroutineScope,
        pluginLogger: Logger,
        conversationManager: ConversationManager
    ) = AskCommand(configManager, plugin, pluginScope, pluginLogger, conversationManager)
}