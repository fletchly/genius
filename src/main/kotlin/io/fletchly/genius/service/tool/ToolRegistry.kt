package io.fletchly.genius.service.tool

import kotlinx.serialization.json.JsonObject

class ToolRegistry {
    private val tools = mutableMapOf<String, ToolDefinition>()

    fun register(tool: ToolDefinition) {
        tools[tool.name] = tool
    }

    fun register(vararg toolDefs: ToolDefinition) {
        toolDefs.forEach { register(it) }
    }

    suspend fun execute(toolName: String, arguments: JsonObject): Result<String> {
        val tool = tools[toolName]
            ?: return Result.failure(IllegalArgumentException("Tool '$toolName' not found"))

        return try {
            val validationErrors = validateArguments(tool, arguments)
            if (validationErrors.isNotEmpty()) {
                return Result.failure(IllegalArgumentException(
                    "Validation errors: ${validationErrors.joinToString(", ")}"
                ))
            }
            Result.success(tool.handler(arguments))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun validateArguments(
        tool: ToolDefinition,
        arguments: JsonObject
    ): List<String> {
        val errors = mutableListOf<String>()

        // Check required parameters
        tool.parameters.filter { it.required }.forEach { param ->
            if (param.name !in arguments.keys) {
                errors.add("Missing required parameter: ${param.name}")
            }
        }

        // Check enum constraints
        tool.parameters.forEach { param ->
            param.enum?.let { allowedValues ->
                arguments[param.name]?.let { value ->
                    if (value.toString() !in allowedValues) {
                        errors.add("${param.name} must be one of: ${allowedValues.joinToString()}")
                    }
                }
            }
        }

        return errors
    }

    fun getTool(name: String): ToolDefinition? = tools[name]

    fun getAllTools(): List<ToolDefinition> = tools.values.toList()
}