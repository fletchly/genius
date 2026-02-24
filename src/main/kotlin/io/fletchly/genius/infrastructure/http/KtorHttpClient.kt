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

package io.fletchly.genius.infrastructure.http

import io.fletchly.genius.infrastructure.config.GeniusConfiguration
import io.fletchly.genius.infrastructure.logging.PluginLogger
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.network.sockets.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import java.io.IOException

private const val REQUEST_TIMEOUT_MS: Long = 2 * 60 * 1000 // 2 minutes
private const val CONNECT_TIMEOUT_MS: Long = 10 * 1000 // 10 seconds
private const val SOCKET_TIMEOUT_MS: Long = 2 * 60 * 1000 // 2 minutes
private const val MAX_RETRIES = 5
private const val BASE_DELAY_MS = 1000L
private const val MAX_DELAY_MS = 60_000L
private const val RANDOMIZATION_MS = 1000L

fun createKtorHttpClient(pluginLogger: PluginLogger, geniusConfiguration: GeniusConfiguration) = HttpClient(CIO) {
    expectSuccess = true
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
            encodeDefaults = true
        })
    }
    install(HttpTimeout) {
        requestTimeoutMillis = REQUEST_TIMEOUT_MS
        connectTimeoutMillis = CONNECT_TIMEOUT_MS
        socketTimeoutMillis = SOCKET_TIMEOUT_MS
    }
    install(HttpRequestRetry) {
        maxRetries = MAX_RETRIES
        retryOnServerErrors()
        retryOnException(retryOnTimeout = true)
        exponentialDelay(
            baseDelayMs = BASE_DELAY_MS,
            maxDelayMs = MAX_DELAY_MS,
            randomizationMs = RANDOMIZATION_MS
        )
    }
    install(Logging) {
        logger = object : Logger {
            override fun log(message: String) {
                pluginLogger.logHttpRequest(message)
            }
        }
        level = LogLevel.BODY
        sanitizeHeader {
            it == HttpHeaders.Authorization
        }
    }
    HttpResponseValidator {
        handleResponseExceptionWithRequest { cause, _ ->
            when (cause) {
                is ClientRequestException -> {
                    pluginLogger.logWarning(
                        "${cause.response.status.toLoggerMessage()}${
                            if (cause.response.status == HttpStatusCode.Unauthorized) " (Is your API key set?)"
                            else ""
                        }"
                    )
                    throw HttpClientException.ClientError(cause.response.status)
                }

                is ServerResponseException -> {
                    pluginLogger.logWarning(cause.response.status.toLoggerMessage())
                    throw HttpClientException.ServerError(cause.response.status)
                }

                is ConnectTimeoutException -> {
                    pluginLogger.logWarning("Request timed out! [${cause.message}]")
                    throw HttpClientException.TimeoutError(cause)
                }

                is IOException -> {
                    pluginLogger.logWarning("Network Error! [${cause.message}]")
                    throw HttpClientException.NetworkError(cause)
                }

                else -> throw cause
            }
        }
    }
}

private fun HttpStatusCode.toLoggerMessage() = "Got $this response from API."
