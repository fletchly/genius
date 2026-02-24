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

package io.fletchly.genius.adapter.outbound.ai.ollama

import io.fletchly.genius.adapter.outbound.ai.ollama.model.OllamaMessage
import io.fletchly.genius.adapter.outbound.ai.ollama.model.OllamaOptions
import io.fletchly.genius.adapter.outbound.ai.ollama.model.OllamaRequest
import io.fletchly.genius.adapter.outbound.ai.ollama.model.OllamaResponse
import io.fletchly.genius.adapter.outbound.ai.ollama.model.toOllamMessage
import io.fletchly.genius.core.exception.AiProviderException
import io.fletchly.genius.core.model.Message
import io.fletchly.genius.core.port.outbound.AiService
import io.fletchly.genius.infrastructure.config.GeniusConfiguration
import io.fletchly.genius.infrastructure.config.SystemPromptManager
import io.fletchly.genius.old.client.HttpClientException
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class OllamaAiService(
    private val httpClient: HttpClient,
    private val configuration: GeniusConfiguration,
    systemPromptManager: SystemPromptManager
) : AiService {
    private val systemPrompt = systemPromptManager.prompt
    private val baseUrl = configuration.ollama.baseUrl
    private val apiKey = configuration.ollama.apiKey

    override suspend fun generateResponse(messages: List<Message>): Message {
        val options = OllamaOptions(
            temperature = configuration.ollama.temperature,
            topK = configuration.ollama.topK,
            topP = configuration.ollama.topP,
            numPredict = configuration.ollama.numPredict
        )

        val systemPromptMessage = OllamaMessage(
            content = systemPrompt,
            role = Message.SYSTEM
        )

        val ollamaMessages = messages.map { it.toOllamMessage() }

        val request = OllamaRequest(
            model = configuration.ollama.model,
            options = options,
            messages = listOf(systemPromptMessage) + ollamaMessages,
            tools = listOf() // FIXME
        )

        try {
            val response =  httpClient.post("$baseUrl/api/chat") {
                if (apiKey != null) bearerAuth(apiKey)
                contentType(ContentType.Application.Json)
                setBody(request)
            }

            return response.body<OllamaResponse>().message.toMessage()
        } catch (_: HttpClientException.ConfigurationError) {
            throw AiProviderException("Configuration has errors")
        } catch (_: HttpClientException.TimeoutError) {
            throw AiProviderException("Request timed out")
        } catch (_: HttpClientException.NetworkError) {
            throw AiProviderException("Network ran into an error")
        } catch (_: HttpClientException.ServerError) {
            throw AiProviderException("External server encountered an error")
        } catch (_: HttpClientException.ClientError) {
            throw AiProviderException("Error with client request")
        } catch (_: Exception) {
            throw AiProviderException("An unknown error occurred")
        }
    }
}