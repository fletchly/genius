/*
 * This file is part of Genius, licensed under the Apache License 2.0
 *
 * Copyright (c) 2026 fletchly
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.fletchly.genius.adapter.outbound.tool

import io.fletchly.genius.core.model.Message
import io.fletchly.genius.core.model.ToolCall
import io.fletchly.genius.core.port.outbound.ToolService
import io.fletchly.genius.infrastructure.logging.PluginLogger
import io.fletchly.genius.infrastructure.tool.Tool
import io.fletchly.genius.infrastructure.tool.ToolDefinition
import io.fletchly.genius.infrastructure.tool.ToolRegistry
import kotlinx.serialization.json.JsonObject

class BasicToolService(
    private val registry: ToolRegistry,
    private val pluginLogger: PluginLogger,
    tools: Set<Tool>
) : ToolService {
    init {
        for (tool in tools) {
            registry.register(tool.definition)
        }
    }

    override suspend fun executeToolCall(toolCall: ToolCall): Message {
        val tool = registry.getToolDefinition(toolCall.name)

        if (tool == null) {
            val errorMessage = toolErrorMessage(toolCall.name, "Tool not found")
            pluginLogger.logToolCallError(errorMessage)
            return Message(
                errorMessage,
                Message.TOOL
            )
        }

        val validationErrors = validateArguments(tool, toolCall.arguments)
        if (validationErrors.isNotEmpty()) {
            val errorMessage =
                toolErrorMessage(tool.name, "Invalid argument(s): ${validationErrors.joinToString { ", " }}")
            pluginLogger.logToolCallError(errorMessage)
            return Message(
                errorMessage,
                Message.TOOL
            )
        }

        try {
            val toolResult = tool.handler(toolCall.arguments)
            pluginLogger.logToolCall("Executed tool: ${tool.name} > $toolResult")
            return Message(
                toolResult,
                Message.TOOL
            )
        } catch (ex: ToolException) {
            val errorMessage = toolErrorMessage(tool.name, "Encountered an exception: $ex")
            pluginLogger.logToolCallError(errorMessage)
            return Message(
                errorMessage,
                Message.TOOL
            )
        } catch (_: Exception) {
            val errorMessage = toolErrorMessage(tool.name, "Encountered an unknown exception")
            return Message(
                errorMessage,
                Message.TOOL
            )
        }
    }

    private fun toolErrorMessage(name: String, message: String) = "Error executing tool '$name': $message"

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
}