package io.fletchly.genius.service.chat.ollama

import io.fletchly.genius.service.tool.ToolDefinition
import io.fletchly.genius.service.tool.ToolRegistry
import kotlinx.serialization.Serializable

@Serializable
data class OllamaTool(
    val type: String = "function",
    val function: OllamaFunction
)

@Serializable
data class OllamaFunction(
    val name: String,
    val description: String,
    val parameters: OllamaParameters
)

@Serializable
data class OllamaParameters(
    val type: String = "object",
    val properties: Map<String, OllamaProperty>,
    val required: List<String>
)

@Serializable
data class OllamaProperty(
    val type: String,
    val description: String
)

fun ToolDefinition.toOllamaTool() : OllamaTool {
    val function = OllamaFunction(
        name = name,
        description = description,
        parameters = OllamaParameters(
            properties = parameters.associate {
                it.name to OllamaProperty(it.type, it.description)
            },
            required = parameters
                .filter { it.required }
                .map { it.name }
        ),
    )

    return OllamaTool(
        function = function
    )
}

fun ToolRegistry.getOllamaToolDefinitions(): List<OllamaTool> {
    return getAllTools().map { it.toOllamaTool() }
}

fun ToolRegistry.getOllamaToolDefinition(name: String): OllamaTool {
    return getTool(name)?.toOllamaTool() ?: throw IllegalArgumentException("Tool $name not found")
}
