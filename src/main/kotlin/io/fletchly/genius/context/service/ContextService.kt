package io.fletchly.genius.context.service

import io.fletchly.genius.conversation.model.Message
import java.util.*

/**
 * Manages player context
 */
interface ContextService {
    /**
     * Add chat to context for given player and return updated context
     */
    suspend fun addChat(message: Message, playerUuid: UUID): List<Message>

    /**
     * Clear context for all players in the current session
     */
    suspend fun clearContext()

    /**
     * Clear context for specified player
     */
    suspend fun clearContext(playerUuid: UUID)
}