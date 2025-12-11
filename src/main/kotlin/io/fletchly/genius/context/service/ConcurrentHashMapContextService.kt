package io.fletchly.genius.context.service

import io.fletchly.genius.config.ConfigManager
import io.fletchly.genius.conversation.model.Message
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import kotlin.collections.ArrayDeque
import kotlin.collections.List
import kotlin.collections.listOf
import kotlin.collections.toList

class ConcurrentHashMapContextService @Inject constructor(configManager: ConfigManager) : ContextService {
    private val maxPlayerMessages = configManager.contextMaxPlayerMessages
    private val playerContext = ConcurrentHashMap<UUID, ArrayDeque<Message>>()

    override suspend fun addChat(
        message: Message,
        playerUuid: UUID
    ): List<Message> {
        val messages = playerContext.compute(playerUuid) { _, messages ->
            val queue = messages ?: ArrayDeque()
            if (queue.size >= maxPlayerMessages) {
                queue.removeFirst()
            }
            queue.add(message)
            queue
        }
        return messages?.toList() ?: listOf(message)
    }

    override suspend fun clearContext() {
        playerContext.clear()
    }

    override suspend fun clearContext(playerUuid: UUID) {
        playerContext.remove(playerUuid)
    }
}