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

package io.fletchly.genius.manager.conversation.service

import io.fletchly.genius.manager.conversation.model.Message
import io.fletchly.genius.service.context.service.ContextService
import io.fletchly.genius.service.ollama.client.GeniusHttpClientException
import io.fletchly.genius.service.ollama.service.ChatService
import java.util.*

/**
 * Manages player conversations
 */
class ConversationManager constructor(
    private val contextService: ContextService,
    private val chatService: ChatService
) {
    /**
     * Generate chat given prompt
     *
     * @param prompt message to use for chat generation
     * @param playerUuid UUID of player sending message
     *
     * @return message with assistant response
     */
    suspend fun chat(prompt: String, playerUuid: UUID): String {
        val playerMessage = Message(
            content = prompt,
            role = Message.USER
        )

        contextService.addChat(playerMessage, playerUuid)

        val playerContext = contextService.getContext(playerUuid)

        try {
            val response = chatService.chat(playerContext)
            contextService.addChat(response, playerUuid)
            return response.content
        } catch (_: GeniusHttpClientException.ConfigurationError) {
            throw ConversationManagerException("Configuration has errors")
        } catch (_: GeniusHttpClientException.TimeoutError) {
            throw ConversationManagerException("Request timed out")
        } catch (_: GeniusHttpClientException.NetworkError) {
            throw ConversationManagerException("Network ran into an error")
        } catch (_: GeniusHttpClientException.ServerError) {
            throw ConversationManagerException("Chat server ran into an error")
        } catch (_: GeniusHttpClientException.ClientError) {
            throw ConversationManagerException("Error with client request")
        }
    }
}