/*
 * This file is part of Genius, licensed under the Apache License 2.0
 *
 * Copyright (c) 2025 fletchly
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

package io.fletchly.genius.service.chat.ollama

import io.fletchly.genius.manager.config.GeniusConfiguration
import io.fletchly.genius.manager.config.SystemPromptManager
import io.fletchly.genius.model.Message
import io.fletchly.genius.client.KtorHttpClient
import io.fletchly.genius.service.chat.ChatService
import io.fletchly.genius.service.tool.ToolRegistry
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class OllamaChatService(
    private val configuration: GeniusConfiguration,
    private val toolRegistry: ToolRegistry,
    systemPromptManager: SystemPromptManager,
    ktorHttpClient: KtorHttpClient

): ChatService {
    private val systemPrompt = systemPromptManager.prompt
    private val baseUrl = configuration.ollama.baseUrl
    private val apiKey = configuration.ollama.apiKey
    private val httpClient = ktorHttpClient.getClient(baseUrl, apiKey)

    override suspend fun chat(messages: List<Message>): Message {
        val ollamaOptions = OllamaOptions(
            temperature = configuration.ollama.temperature,
            topK = configuration.ollama.topK,
            topP = configuration.ollama.topP,
            numPredict = configuration.ollama.numPredict
        )

        val systemPromptMessage = Message(
            content = systemPrompt,
            role = Message.Role.SYSTEM
        )

        val request = OllamaRequest(
            model = configuration.ollama.model,
            options = ollamaOptions,
            messages = listOf(systemPromptMessage) + messages,
            tools = toolRegistry.getOllamaToolDefinitions()
        )

        val response = httpClient.post("/api/chat") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        return response.body<OllamaResponse>().message
    }
}