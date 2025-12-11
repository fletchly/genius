package io.fletchly.genius

import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class PluginModule(private val plugin: Genius) {
    @Provides
    @Singleton
    fun providePlugin() = plugin

    @Provides
    @Singleton
    fun providePluginConfig() = plugin.config

    @Provides
    @Singleton
    fun providePluginLogger() = plugin.logger

    @Provides
    @Singleton
    fun providePluginScope() = plugin.scope
}