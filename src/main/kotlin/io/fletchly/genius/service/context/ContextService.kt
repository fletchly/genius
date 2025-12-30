package io.fletchly.genius.service.context

import io.fletchly.genius.model.Message
import java.util.UUID

/**
 * Manages player conversation context
 */
interface ContextService {
    /**
     * Get context for given player
     */
    suspend fun getContext(playerUuid: UUID): List<Message>

    /**
     * Add chat to context for given player
     */
    suspend fun addChat(message: Message, playerUuid: UUID)

    /**
     * Clear context for all players in the current session
     */
    suspend fun clearContext()

    /**
     * Clear context for specific player
     */
    suspend fun clearContext(playerUuid: UUID)
}