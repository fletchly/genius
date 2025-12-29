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

import io.fletchly.genius.manager.conversation.model.Message
import java.util.*

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