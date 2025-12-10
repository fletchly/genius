package io.fletchly.genius.ollama.model

import io.fletchly.genius.conversation.model.Message

data class OllamaRequest(
    val model: String,
    val messages: List<Message>,
    val options: OllamaOptions,
    val stream: Boolean = false,
    val think: Boolean = false
)