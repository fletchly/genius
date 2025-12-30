package io.fletchly.genius.service.tool

import io.fletchly.genius.model.Message
import io.fletchly.genius.model.ToolCall
import kotlinx.serialization.json.*

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
        registry.execute(toolCall.function.name, toolCall.function.arguments.toPrimitiveMap())
            .onSuccess {
                return Message(
                    content = it.toString(),
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

    private fun Map<String, JsonElement>.toPrimitiveMap(): Map<String, Any> {
        return this.mapValues { (_, element) ->
            element.toPrimitiveValue()
        }
    }

    private fun JsonElement.toPrimitiveValue(): Any {
        return when (this) {
            is JsonPrimitive -> {
                if (isString) content
                else when {
                    booleanOrNull != null -> boolean
                    longOrNull != null -> long
                    doubleOrNull != null -> double
                    else -> content // fallback (should not happen)
                }
            }
            is JsonObject -> this.toPrimitiveMap() // recursive for nested objects
            is JsonArray -> this.map { it.toPrimitiveValue() } // recursive for arrays
        }
    }
}