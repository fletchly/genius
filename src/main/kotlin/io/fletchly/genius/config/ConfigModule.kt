package io.fletchly.genius.config

import dagger.Module
import dagger.Provides
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
    fun providePromptManager(plugin: JavaPlugin) = PromptManager(plugin)
}