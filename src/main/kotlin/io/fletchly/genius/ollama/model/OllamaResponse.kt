package io.fletchly.genius.ollama.model

import io.fletchly.genius.conversation.Message
import kotlinx.serialization.SerialName

data class OllamaResponse(
    val model: String,
    @SerialName("created_at")
    val createdAt: String,
    val message: Message
)
