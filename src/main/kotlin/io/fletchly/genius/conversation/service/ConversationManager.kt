package io.fletchly.genius.conversation.service

import io.fletchly.genius.context.service.ContextService
import io.fletchly.genius.conversation.model.Message
import io.fletchly.genius.ollama.service.ChatService
import java.util.*
import javax.inject.Inject

/**
 * Manages player conversations
 */
class ConversationManager @Inject constructor(
    private val contextService: ContextService,
    private val chatService: ChatService
) {
    /**
     * Generate chat given prompt
     */
    suspend fun generateChat(prompt: String, playerUUID: UUID): String {
        val playerMessage = Message(
            content = prompt,
            role = Message.USER
        )

        // Get context
        val playerContext = contextService.addChat(playerMessage, playerUUID)

        // Get response
        val response = chatService.generateChat(playerContext)

        return response.content
    }
}