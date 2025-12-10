package io.fletchly.genius.conversation

import dagger.Module
import dagger.Provides
import io.fletchly.genius.context.service.ContextService
import io.fletchly.genius.conversation.service.ConversationManagerService
import io.fletchly.genius.ollama.service.ChatService
import javax.inject.Singleton

@Module
class ConversationModule {
    @Provides
    @Singleton
    fun provideConversationManagerService(contextService: ContextService, chatService: ChatService) =
        ConversationManagerService(contextService, chatService)
}