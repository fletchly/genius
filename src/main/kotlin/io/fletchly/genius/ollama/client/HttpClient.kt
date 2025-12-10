package io.fletchly.genius.ollama.client

import io.fletchly.genius.ollama.model.OllamaRequest
import io.fletchly.genius.ollama.model.OllamaResponse

interface HttpClient {
    /**
     * Fetch chat response from Ollama API
     */
    suspend fun fetchChatResponse(request: OllamaRequest): OllamaResponse
}