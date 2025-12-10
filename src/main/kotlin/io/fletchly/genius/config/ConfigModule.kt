package io.fletchly.genius.config

import dagger.Module
import dagger.Provides
import io.fletchly.genius.Genius
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.plugin.java.JavaPlugin
import javax.inject.Singleton

@Module
class ConfigModule {
    @Provides
    @Singleton
    fun provideConfigManager(config: FileConfiguration) = ConfigManager(config)

    @Provides
    @Singleton
    fun providePromptManager(plugin: Genius) = PromptManager(plugin)
}