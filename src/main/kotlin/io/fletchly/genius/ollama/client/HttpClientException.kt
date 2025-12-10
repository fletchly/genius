package io.fletchly.genius.ollama.client

/**
 * Exception to handle HttpClient errors
 */
class HttpClientException(message: String, cause: Throwable?) : Exception(message, cause)