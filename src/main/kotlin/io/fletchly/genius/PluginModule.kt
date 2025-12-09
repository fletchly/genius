package io.fletchly.genius

import dagger.Module
import dagger.Provides
import org.bukkit.plugin.java.JavaPlugin
import javax.inject.Singleton

@Module
class PluginModule(private val plugin: JavaPlugin) {
    @Provides
    @Singleton
    fun providePlugin() = plugin

    @Provides
    @Singleton
    fun providePluginConfig() = plugin.config
}