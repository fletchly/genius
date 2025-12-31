package io.fletchly.genius.service.tool

import io.fletchly.genius.model.Message
import io.fletchly.genius.model.ToolCall

class ToolService(
    tools: List<Tool>
) {
    private val registry = ToolRegistry()
    init {
        for (tool in tools) {
            registry.register(tool.definition)
        }
    }

    suspend fun executeToolCall(toolCall: ToolCall): Message {
        registry.execute(toolCall.function.name, toolCall.function.arguments)
            .onSuccess {
                return Message(
                    content = it,
                    role = Message.TOOL
                )
            }
            .onFailure {
                return Message(
                    content = "Error: ${it.message}",
                    role = Message.TOOL
                )
            }
        return Message(
            content = "An unknown error occurred",
            role = Message.TOOL
        )
    }
}