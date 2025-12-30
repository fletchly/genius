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

package io.fletchly.genius.manager.conversation

import io.fletchly.genius.model.Message
import io.fletchly.genius.service.context.ContextService
import io.fletchly.genius.client.HttpClientException
import io.fletchly.genius.service.chat.ChatService
import io.fletchly.genius.service.tool.ToolService
import java.util.*

/**
 * Manages player conversations
 */
class ConversationManager(
    private val contextService: ContextService,
    private val chatService: ChatService,
    private val toolService: ToolService
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

        val response = withHttpClientErrorHandling {
            val response = chatService.chat(playerContext) // Suspend Call
            contextService.addChat(response, playerUuid) // Suspend Call
            response.content
        }

        return response
    }

    private suspend inline fun <T> withHttpClientErrorHandling(block: suspend () -> T): T {
        return try {
            block()
        } catch (_: HttpClientException.ConfigurationError) {
            throw ConversationManagerException("Configuration has errors")
        } catch (_: HttpClientException.TimeoutError) {
            throw ConversationManagerException("Request timed out")
        } catch (_: HttpClientException.NetworkError) {
            throw ConversationManagerException("Network ran into an error")
        } catch (_: HttpClientException.ServerError) {
            throw ConversationManagerException("Chat server ran into an error")
        } catch (_: HttpClientException.ClientError) {
            throw ConversationManagerException("Error with client request")
        }
    }
}