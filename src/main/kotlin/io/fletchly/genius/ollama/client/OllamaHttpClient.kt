package io.fletchly.genius.ollama.client

import io.fletchly.genius.config.ConfigManager
import io.fletchly.genius.ollama.model.OllamaRequest
import io.fletchly.genius.ollama.model.OllamaResponse
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import javax.inject.Inject

/**
 * Http client for interacting with the Ollama API
 */
class OllamaHttpClient @Inject constructor(configManager: ConfigManager): HttpClient {
    val baseUrl = configManager.ollamaBaseUrl
    // Throw exception if no API key is provided
    val apiKey = configManager.ollamaApiKey ?: throw HttpClientException("No Ollama API key provided!", null)

    /**
     * Internal Ktor client
     */
    private val client = io.ktor.client.HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                encodeDefaults = true
            })
        }
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.ALL
        }
        defaultRequest {
            url(baseUrl)
            header(HttpHeaders.Authorization, "Bearer $apiKey")
        }
    }

    /**
     * Fetch chat response from Ollama API
     */
    override suspend fun fetchChatResponse(request: OllamaRequest): OllamaResponse {
        return try {
            val response: HttpResponse = client.post {
                url {
                    appendPathSegments("api", "chat")
                }
                contentType(ContentType.Application.Json)
                setBody(request)
            }
            handleResponse(response)
        } catch (e: Exception) {
            throw HttpClientException("Request to Ollama API failed: ${e.message}", e)
        }
    }

    /**
     * Handle Http responses
     */
    private suspend inline fun <reified T> handleResponse(response: HttpResponse): T {
        if (response.status.isSuccess()) {
            return response.body()
        } else {
            throw HttpClientException("Request to Ollama API failed with status: ${response.status}", null)
        }
    }
}