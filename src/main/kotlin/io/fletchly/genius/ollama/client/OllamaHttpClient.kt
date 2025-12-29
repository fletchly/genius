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

import io.fletchly.genius.config.GeniusConfiguration
import io.fletchly.genius.ollama.model.OllamaRequest
import io.fletchly.genius.ollama.model.OllamaResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import java.io.IOException
import java.util.logging.Logger
import javax.inject.Inject

/**
 * Http client for interacting with the Ollama API
 */
class OllamaHttpClient @Inject constructor(
    private val pluginLogger: Logger,
    configuration: GeniusConfiguration
) : GeniusHttpClient<OllamaRequest, OllamaResponse> {
    private val baseUrl = configuration.ollama.baseUrl
    private val apiKey = configuration.ollama.apiKey

    override val ktorClient: HttpClient by lazy {
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
                if (apiKey != null) bearerAuth(apiKey)
            }
        }
    }

    override suspend fun chat(request: OllamaRequest): OllamaResponse =
        try {
            val response = ktorClient.post("/api/chat") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }

            when (response.status.value) {
                401 -> {
                    pluginLogger.warning { "[OllamaHttpClient] Got ${response.status.value} Unauthorized response from Ollama API. (Is your API key set?)" }
                    throw GeniusHttpClientException.ClientError(response.status)
                }

                in 400..499 -> {
                    pluginLogger.warning {
                        "[OllamaHttpClient] Got ${response.status.value} Client Error response from Ollama API! [${response}]"
                    }
                    throw GeniusHttpClientException.ClientError(response.status)
                }

                in 500..599 -> {
                    pluginLogger.warning {
                        "[OllamaHttpClient] Got ${response.status.value} Server Error Error response from Ollama API! [${response}]"
                        throw GeniusHttpClientException.ServerError(response.status)
                    }
                }
            }
            response.body<OllamaResponse>()
        } catch (e: HttpRequestTimeoutException) {
            pluginLogger.warning { "[OllamaHttpClient] Request timed out! [${e.message}]" }
            throw GeniusHttpClientException.TimeoutError(e)
        } catch (e: IOException) {
            pluginLogger.warning { "[OllamaHttpClient] Network Error! [${e.message}]" }
            throw GeniusHttpClientException.NetworkError(e)
        }

    private companion object {
        const val REQUEST_TIMEOUT_MILLIS: Long = 2 * 60 * 1000 // 2 minutes
        const val CONNECT_TIMEOUT_MILLIS: Long = 10 * 1000 // 10 seconds
        const val SOCKET_TIMEOUT_MILLIS: Long = 2 * 60 * 1000 // 2 minutes
        const val MAX_RETRIES = 5
    }
}