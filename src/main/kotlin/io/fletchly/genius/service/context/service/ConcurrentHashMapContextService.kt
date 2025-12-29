/*
 * This file is part of Genius, licensed under the Apache License 2.0.
 *
 * Copyright (c) 2025 fletchly
 * Copyright (c) 2025 contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.fletchly.genius.service.context.service

import io.fletchly.genius.manager.config.GeniusConfiguration
import io.fletchly.genius.manager.conversation.model.Message
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.ArrayDeque

/**
 * [ContextService] implementation that stores conversation context in a [ConcurrentHashMap]
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