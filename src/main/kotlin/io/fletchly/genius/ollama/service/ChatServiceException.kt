package io.fletchly.genius.ollama.service

class ChatServiceException(message: String, cause: Throwable?): Exception(message, cause)