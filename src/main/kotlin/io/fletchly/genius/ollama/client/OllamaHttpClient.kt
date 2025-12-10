package io.fletchly.genius.ollama.client

import io.fletchly.genius.config.ConfigManager
import io.fletchly.genius.ollama.model.OllamaRequest
import io.fletchly.genius.ollama.model.OllamaResponse
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.http.headers
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import javax.inject.Inject

/**
 * Http client for interacting with the Ollama API
 */
class OllamaHttpClient @Inject constructor(private val config: ConfigManager): HttpClient {
    /**
     * Internal Ktor client
     */
    private val client = io.ktor.client.HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }

    /**
     * Fetch chat response from Ollama API
     */
    override suspend fun fetchChatResponse(request: OllamaRequest): OllamaResponse {
        val baseUrl = config.ollamaBaseUrl
        // Throw exception if no API key is provided
        val apiKey = config.ollamaApiKey ?: throw HttpClientException("No Ollama API key provided!", null)

        return try {
            val response: HttpResponse = client.post(baseUrl) {
                url {
                    appendPathSegments("api", "chat")
                }
                headers {
                    append(HttpHeaders.Authorization, "Bearer $apiKey")
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