package io.fletchly.genius.listeners

import dagger.Module
import dagger.Provides
import io.fletchly.genius.context.service.ContextService
import kotlinx.coroutines.CoroutineScope
import java.util.logging.Logger

@Module
class ListenerModule {
    @Provides
    fun providePlayerListener(
        pluginLogger: Logger,
        pluginScope: CoroutineScope,
        contextService: ContextService
        ) = PlayerListener(pluginLogger, pluginScope, contextService)
}