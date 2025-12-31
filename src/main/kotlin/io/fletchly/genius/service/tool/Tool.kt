package io.fletchly.genius.service.tool

import kotlinx.serialization.json.JsonObject

interface Tool {
    val definition: ToolDefinition
    suspend fun handleTool(args: JsonObject): String
}