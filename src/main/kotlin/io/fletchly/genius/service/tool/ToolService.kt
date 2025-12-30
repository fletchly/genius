package io.fletchly.genius.service.tool

class ToolService(
    tools: List<Tool>
) {
    private val registry = ToolRegistry()
    init {
        for (tool in tools) {
            registry.register(tool.definition)
        }
    }


}