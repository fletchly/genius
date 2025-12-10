package io.fletchly.genius.context.service

import io.fletchly.genius.config.ConfigManager
import io.fletchly.genius.conversation.Message
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

class ConcurrentHashMapContextService @Inject constructor(configManager: ConfigManager): ContextService {
    private val maxPlayerMessages= configManager.contextMaxPlayerMessages
    private val playerContext = ConcurrentHashMap<UUID, ArrayDeque<Message>>()

    override suspend fun addChat(
        message: Message,
        playerUUID: UUID
    ): List<Message> {
        val messages =  playerContext.compute(playerUUID) { _, messages ->
            val queue = messages ?: ArrayDeque()
            if (queue.size >= maxPlayerMessages) {
                queue.removeFirst()
            }
            queue.add(message)
            queue
        }
        return messages?.toList() ?: listOf(message)
    }
}