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

package io.fletchly.genius.ollama.service

import io.fletchly.genius.config.manager.ConfigurationManager
import io.fletchly.genius.config.manager.SystemPromptManager
import io.fletchly.genius.conversation.model.Message
import io.fletchly.genius.ollama.client.HttpClient
import io.fletchly.genius.ollama.client.HttpClientException
import io.fletchly.genius.ollama.model.OllamaOptions
import io.fletchly.genius.ollama.model.OllamaRequest
import javax.inject.Inject

class OllamaChatService @Inject constructor(
    private val configurationManager: ConfigurationManager,
    private val systemPromptManager: SystemPromptManager,
    private val httpClient: HttpClient
) : ChatService {
    override suspend fun generateChat(messages: List<Message>): Message {
        // Build Ollama response parameters
        val ollamaOptions = OllamaOptions(
            temperature = configurationManager.ollamaTemperature,
            topK = configurationManager.ollamaTopK,
            topP = configurationManager.ollamaTopP,
            numPredict = configurationManager.ollamaNumPredict
        )

        // Build system prompt
        val systemPromptMessage = Message(
            content = systemPromptManager.prompt,
            role = Message.SYSTEM
        )

        // Build request
        val request = OllamaRequest(
            model = configurationManager.ollamaModel,
            options = ollamaOptions,
            messages = listOf(systemPromptMessage) + messages
        )

        // Use Http client to fetch response
        return try {
            httpClient.fetchChatResponse(request).message
        } catch (httpClientException: HttpClientException) {
            throw ChatServiceException("An HTTP error occurred: ${httpClientException.message}", httpClientException)
        } catch (e: Exception) {
            throw ChatServiceException("An unknown error occured: ${e.message}", e)
        }
    }
}