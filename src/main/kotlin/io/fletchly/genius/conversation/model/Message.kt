package io.fletchly.genius.conversation.model

data class Message(
    val content: String,
    val role: String
) {
    companion object Role {
        const val SYSTEM = "system"
        const val USER = "user"
        const val ASSISTANT = "assistant"
        const val TOOL = "tool"
    }
}