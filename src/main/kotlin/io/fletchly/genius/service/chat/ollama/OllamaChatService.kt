package io.fletchly.genius.service.chat.ollama

import io.fletchly.genius.manager.config.GeniusConfiguration
import io.fletchly.genius.manager.config.SystemPromptManager
import io.fletchly.genius.model.Message
import io.fletchly.genius.client.KtorHttpClient
import io.fletchly.genius.service.chat.ChatService
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class OllamaChatService(
    private val configuration: GeniusConfiguration,
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
            messages = listOf(systemPromptMessage) + messages
        )

        val response = httpClient.post("/api/chat") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        return response.body<OllamaResponse>().message
    }
}