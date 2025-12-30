package io.fletchly.genius.service.tool

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

    fun executeToolCall(toolCall: ToolCall) {

    }
}