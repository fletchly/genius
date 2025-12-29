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

package io.fletchly.genius.service.ollama.service

import io.fletchly.genius.manager.config.GeniusConfiguration
import io.fletchly.genius.manager.config.SystemPromptManager
import io.fletchly.genius.manager.conversation.model.Message
import io.fletchly.genius.service.ollama.client.GeniusHttpClient
import io.fletchly.genius.service.ollama.model.OllamaOptions
import io.fletchly.genius.service.ollama.model.OllamaRequest
import io.fletchly.genius.service.ollama.model.OllamaResponse

class OllamaChatService(
    private val configuration: GeniusConfiguration,
    private val systemPromptManager: SystemPromptManager,
    private val httpClient: GeniusHttpClient<OllamaRequest, OllamaResponse>
) : ChatService {
    override suspend fun chat(messages: List<Message>): Message {
        val ollamaOptions = OllamaOptions(
            temperature = configuration.ollama.temperature,
            topK = configuration.ollama.topK,
            topP = configuration.ollama.topP,
            numPredict = configuration.ollama.numPredict
        )

        val systemPromptMessage = Message(
            content = systemPromptManager.prompt,
            role = Message.SYSTEM
        )

        val request = OllamaRequest(
            model = configuration.ollama.model,
            options = ollamaOptions,
            messages = listOf(systemPromptMessage) + messages
        )

        return httpClient.chat(request).message
    }
}