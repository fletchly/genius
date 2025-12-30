package io.fletchly.genius.service.context

import io.fletchly.genius.manager.config.GeniusConfiguration
import io.fletchly.genius.model.Message
import io.fletchly.genius.service.context.ContextService
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * [ContextService] implementation that stores conversation context in a [java.util.concurrent.ConcurrentHashMap]
 */
class ConcurrentHashMapContextService(configuration: GeniusConfiguration) : ContextService {
    private val maxPlayerMessages = configuration.context.maxPlayerMessages
    private val playerContext = ConcurrentHashMap<UUID, ArrayDeque<Message>>()

    override suspend fun getContext(playerUuid: UUID): List<Message> {
        return playerContext[playerUuid]?.toList() ?: listOf()
    }

    override suspend fun addChat(
        message: Message,
        playerUuid: UUID
    ) {
        playerContext.compute(playerUuid) { _, messages ->
            val queue = messages ?: ArrayDeque()
            if (queue.size >= maxPlayerMessages) {
                queue.removeFirst()
            }
            queue.add(message)
            queue
        }
    }

    override suspend fun clearContext() {
        playerContext.clear()
    }

    override suspend fun clearContext(playerUuid: UUID) {
        playerContext.remove(playerUuid)
    }
}