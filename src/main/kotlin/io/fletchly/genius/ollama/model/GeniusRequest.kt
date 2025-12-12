package io.fletchly.genius.ollama.model

import io.fletchly.genius.conversation.model.Message

interface GeniusRequest {
    val messages: List<Message>
}