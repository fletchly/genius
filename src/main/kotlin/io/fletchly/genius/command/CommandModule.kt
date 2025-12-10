package io.fletchly.genius.command

import dagger.Module
import dagger.Provides
import io.fletchly.genius.config.ConfigManager
import javax.inject.Singleton

@Module
class CommandModule {
    @Provides
    @Singleton
    fun provideAskCommand(configManager: ConfigManager) = AskCommand(configManager)
}