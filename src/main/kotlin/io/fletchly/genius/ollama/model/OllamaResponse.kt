package io.fletchly.genius.ollama.model

import io.fletchly.genius.conversation.model.Message
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OllamaResponse(
    val model: String,
    @SerialName("created_at")
    val createdAt: String,
    val message: Message
)
