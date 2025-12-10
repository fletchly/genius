package io.fletchly.genius.context

import dagger.Module
import dagger.Provides
import io.fletchly.genius.config.ConfigManager
import io.fletchly.genius.context.service.ConcurrentHashMapContextService
import io.fletchly.genius.context.service.ContextService
import javax.inject.Singleton

@Module
class ContextModule {
    @Provides
    @Singleton
    fun provideContextService(configManager: ConfigManager): ContextService = ConcurrentHashMapContextService(configManager)
}