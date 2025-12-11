package io.fletchly.genius.ollama.model

import io.fletchly.genius.conversation.model.Message
import kotlinx.serialization.Serializable

@Serializable
data class OllamaRequest(
    val model: String,
    val messages: List<Message>,
    val options: OllamaOptions,
    val stream: Boolean = false,
    val think: Boolean = false
)