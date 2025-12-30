package io.fletchly.genius.service.chat

import io.fletchly.genius.model.Message

/**
 * Service for generating chats from assistant
 */
interface ChatService {
    /**
     * Generate chat
     *
     * @param messages context to send assistant
     * @return the assistant's response
     */
    suspend fun chat(messages: List<Message>): Message
}