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