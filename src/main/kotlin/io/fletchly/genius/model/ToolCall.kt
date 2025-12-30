package io.fletchly.genius.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class ToolCall(
    val function: ToolCallFunction
)

@Serializable
data class ToolCallFunction(
    val name: String,
    val arguments: Map<String, JsonElement>
)
