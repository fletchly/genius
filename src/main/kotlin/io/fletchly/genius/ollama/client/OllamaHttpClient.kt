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
import io.ktor.client.HttpClient
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import javax.inject.Inject

/**
 * Http client for interacting with the Ollama API
 */
class OllamaHttpClient @Inject constructor(configurationManager: ConfigurationManager) : GeniusHttpClient {
    private val baseUrl: String by lazy { configurationManager.ollamaBaseUrl }
    private val apiKey: String by lazy {
        configurationManager.ollamaApiKey
            ?: throw HttpClientException("No Ollama API key provided!", null)
    }

    private val client: HttpClient by lazy {
        HttpClient(CIO) {
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
                maxRetries = MAX_RETRIES
                retryOnServerErrors()
                retryOnException(retryOnTimeout = true)
                exponentialDelay(
                    baseDelayMs = 1000L,
                    maxDelayMs = 60_000L,
                    randomizationMs = 1000L
                )
            }
            defaultRequest {
                url(baseUrl)
                bearerAuth(apiKey)
            }
        }
    }

    /**
     * Generate chat using Ollama API
     *
     * @param request structured request for Ollama API
     * @return structured response from Ollama API
     */
    override suspend fun chat(request: OllamaRequest): OllamaResponse =
        try {
            client.post("/api/chat") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }.body<OllamaResponse>()
        } catch (e: Exception) {
            throw HttpClientException("Request to Ollama API failed: ${e.message}", e)
        }

    private companion object {
        const val REQUEST_TIMEOUT_MILLIS: Long = 10 * 60 * 1000 // 10 minutes
        const val CONNECT_TIMEOUT_MILLIS: Long = 10 * 1000 // 10 seconds
        const val SOCKET_TIMEOUT_MILLIS: Long = 10 * 60 * 1000 // 10 minutes
        const val MAX_RETRIES = 5
    }
}