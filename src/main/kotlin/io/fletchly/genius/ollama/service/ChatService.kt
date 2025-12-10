package io.fletchly.genius.ollama.service

import io.fletchly.genius.conversation.model.Message

/**
 * Service for generating chats from assistant
 */
interface ChatService {
    suspend fun generateChat(messages: List<Message>): Message
}