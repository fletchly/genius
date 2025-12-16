package io.fletchly.genius.ollama.tool

class ToolRegistry(vararg tools: Tool<out Any, out Any>) {
    val tools = listOf(*tools)
}