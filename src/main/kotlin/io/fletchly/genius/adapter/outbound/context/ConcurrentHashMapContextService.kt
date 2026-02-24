/*
 * This file is part of Genius, licensed under the Apache License 2.0
 *
 * Copyright (c) 2026 fletchly
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

package io.fletchly.genius.adapter.outbound.context

import io.fletchly.genius.core.model.Message
import io.fletchly.genius.core.port.outbound.ContextService
import io.fletchly.genius.infrastructure.config.GeniusConfiguration
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * [ConcurrentHashMap] implementation of [ContextService]
 */
class ConcurrentHashMapContextService(configuration: GeniusConfiguration): ContextService {
    private val maxPlayerMessages = configuration.context.maxPlayerMessages
    private val context = ConcurrentHashMap<UUID, ArrayDeque<Message>>()

    override suspend fun getContext(playerUUID: UUID) = context[playerUUID]?.toList() ?: listOf()

    override suspend fun appendContext(playerUUID: UUID, message: Message) {
        context.compute(playerUUID) { _, messages, ->
            val queue = messages ?: ArrayDeque()
            if (queue.size >= maxPlayerMessages) {
                queue.removeFirst()
            }
            queue.add(message)
            queue
        }
    }

    override suspend fun clearContext(playerUUID: UUID) {
        context.remove(playerUUID)
    }

    override suspend fun clearContext() {
        context.clear()
    }
}