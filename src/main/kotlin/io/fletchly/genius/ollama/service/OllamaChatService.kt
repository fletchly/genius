package io.fletchly.genius.ollama.service

import io.fletchly.genius.config.ConfigManager
import io.fletchly.genius.config.PromptManager
import io.fletchly.genius.conversation.model.Message
import io.fletchly.genius.ollama.client.HttpClient
import io.fletchly.genius.ollama.client.HttpClientException
import io.fletchly.genius.ollama.model.OllamaOptions
import io.fletchly.genius.ollama.model.OllamaRequest
import javax.inject.Inject

class OllamaChatService @Inject constructor(
    private val configManager: ConfigManager,
    private val promptManager: PromptManager,
    private val httpClient: HttpClient
) : ChatService {
    override suspend fun generateChat(messages: List<Message>): Message {
        // Build Ollama response parameters
        val ollamaOptions = OllamaOptions(
            temperature = configManager.ollamaTemperature,
            topK = configManager.ollamaTopK,
            topP = configManager.ollamaTopP,
            numPredict = configManager.ollamaNumPredict
        )

        // Build system prompt
        val systemPromptMessage = Message(
            content = promptManager.prompt,
            role = Message.SYSTEM
        )

        // Build request
        val request = OllamaRequest(
            model = configManager.ollamaModel,
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