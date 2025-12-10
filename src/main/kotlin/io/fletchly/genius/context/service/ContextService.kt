package io.fletchly.genius.context.service

import io.fletchly.genius.conversation.Message
import java.util.UUID

/**
 * Manages player context
 */
interface ContextService {
    /**
     * Add chat to context for given player and return updated context
     */
    suspend fun addChat(message: Message, playerUUID: UUID): List<Message>
}