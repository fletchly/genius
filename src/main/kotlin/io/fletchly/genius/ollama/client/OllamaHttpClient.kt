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

package io.fletchly.genius.ollama.client

import io.fletchly.genius.config.manager.ConfigurationManager
import io.fletchly.genius.ollama.model.OllamaRequest
import io.fletchly.genius.ollama.model.OllamaResponse
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import javax.inject.Inject

/**
 * Http client for interacting with the Ollama API
 */
class OllamaHttpClient @Inject constructor(configurationManager: ConfigurationManager) : HttpClient {
    private val baseUrl = configurationManager.ollamaBaseUrl

    // Throw exception if no API key is provided
    private val apiKey = configurationManager.ollamaApiKey ?: throw HttpClientException("No Ollama API key provided!", null)

    private val client = io.ktor.client.HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                encodeDefaults = true
            })
        }
        install(HttpTimeout) {
            requestTimeoutMillis = REQUEST_TIMEOUT_MILLIS
            connectTimeoutMillis = CONNECT_TIMEOUT_MILLIS
            socketTimeoutMillis = SOCKET_TIMEOUT_MILLIS
        }
        install(HttpRequestRetry) {
            retryOnServerErrors(maxRetries = MAX_RETRIES)
            retryOnException(maxRetries = MAX_RETRIES, retryOnTimeout = true)
            exponentialDelay(
                baseDelayMs = 1000L,
                maxDelayMs = 60000L,
                randomizationMs = 1000L
            )
        }
        defaultRequest {
            url(baseUrl)
            header(HttpHeaders.Authorization, "Bearer $apiKey")
        }
    }

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

    private suspend inline fun <reified T> handleResponse(response: HttpResponse): T {
        if (response.status.isSuccess()) {
            return response.body()
        } else {
            throw HttpClientException("Request to Ollama API failed with status: ${response.status}", null)
        }
    }

    private companion object ClientOptions {
        const val REQUEST_TIMEOUT_MILLIS: Long = 10 * 60 * 1000 // 10 minutes
        const val CONNECT_TIMEOUT_MILLIS: Long = 10 * 1000 // 10 seconds
        const val SOCKET_TIMEOUT_MILLIS: Long = 10 * 60 * 1000 // 10 minutes
        const val MAX_RETRIES = 5
    }
}