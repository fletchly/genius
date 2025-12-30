package io.fletchly.genius.client

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.network.sockets.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import java.io.IOException
import java.util.logging.Logger

class KtorHttpClient(
    @PublishedApi
    internal val logger: Logger
) {
    fun getClient(baseUrl: String, apiKey: String?) = HttpClient(CIO) {
        expectSuccess = true
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
        HttpResponseValidator {
            handleResponseExceptionWithRequest { cause, _ ->
                when (cause) {
                    is ClientRequestException -> {
                        logger.warning { "${cause.response.status.toLoggerMessage()}${if (cause.response.status == HttpStatusCode.Unauthorized) " (Is your API key set?)" else ""}" }
                        throw HttpClientException.ClientError(cause.response.status)
                    }

                    is ServerResponseException -> {
                        logger.warning { cause.response.status.toLoggerMessage() }
                        throw HttpClientException.ServerError(cause.response.status)
                    }

                    is ConnectTimeoutException -> {
                        logger.warning { "Request timed out! [${cause.message}]" }
                        throw HttpClientException.TimeoutError(cause)
                    }

                    is IOException -> {
                        logger.warning { "Network Error! [${cause.message}]" }
                        throw HttpClientException.NetworkError(cause)
                    }

                    else -> throw cause
                }
            }
        }
    }

    private fun HttpStatusCode.toLoggerMessage() = "Got $this response from API."

    private companion object {
        const val REQUEST_TIMEOUT_MILLIS: Long = 2 * 60 * 1000 // 2 minutes
        const val CONNECT_TIMEOUT_MILLIS: Long = 10 * 1000 // 10 seconds
        const val SOCKET_TIMEOUT_MILLIS: Long = 2 * 60 * 1000 // 2 minutes
        const val MAX_RETRIES = 5
    }
}