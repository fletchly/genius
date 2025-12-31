package io.fletchly.genius.service.tool

import kotlinx.serialization.json.JsonObject

data class ToolDefinition(
    val name: String,
    val description: String,
    val parameters: List<ToolParameter>,
    val handler: suspend (JsonObject) -> String
)

data class ToolParameter(
    val name: String,
    val type: String,
    val description: String,
    val required: Boolean = true,
    val enum: List<String>? = null
)

class ToolBuilder {
    var name: String = ""
    var description: String = ""
    private val parameters = mutableListOf<ToolParameter>()
    private var handler: suspend (JsonObject) -> String = { "" }

    fun parameter(
        name: String,
        type: String,
        description: String,
        required: Boolean = true,
        enum: List<String>? = null
    ) {
        parameters.add(ToolParameter(name, type, description, required, enum))
    }

    fun handle(block: suspend (JsonObject) -> String) {
        handler = block
    }

    fun build() = ToolDefinition(name, description, parameters, handler)
}

fun tool(block: ToolBuilder.() -> Unit): ToolDefinition {
    return ToolBuilder().apply(block).build()
}