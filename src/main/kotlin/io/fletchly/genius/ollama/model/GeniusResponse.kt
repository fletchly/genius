package io.fletchly.genius.ollama.model

import io.fletchly.genius.conversation.model.Message

interface GeniusResponse {
    val message: Message
}