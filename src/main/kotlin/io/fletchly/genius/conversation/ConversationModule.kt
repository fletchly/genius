package io.fletchly.genius.conversation

import dagger.Module
import dagger.Provides
import io.fletchly.genius.context.service.ContextService
import io.fletchly.genius.conversation.service.ConversationManager
import io.fletchly.genius.ollama.service.ChatService
import javax.inject.Singleton

@Module
class ConversationModule {
    @Provides
    @Singleton
    fun provideConversationManager(contextService: ContextService, chatService: ChatService) =
        ConversationManager(contextService, chatService)
}