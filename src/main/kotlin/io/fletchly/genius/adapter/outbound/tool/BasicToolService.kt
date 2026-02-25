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
import io.fletchly.genius.infrastructure.tool.ToolData
import io.fletchly.genius.infrastructure.tool.ToolRegistry
import io.fletchly.genius.infrastructure.tool.ToolResult

class BasicToolService(
    private val registry: ToolRegistry,
    private val pluginLogger: PluginLogger,
    tools: Set<Tool>
) : ToolService {
    init {
        tools.forEach {
            registry.register(it)
        }
    }

    override suspend fun executeToolCall(toolCall: ToolCall): Message {
        val toolData: ToolData = when (val result = registry.invoke(toolCall.name, toolCall.arguments)) {
            is ToolResult.Success -> ToolData(toolName = toolCall.name, success = true, data = result.value)
            is ToolResult.Failure -> ToolData(toolName = toolCall.name, success = false, error = result.message)
        }

        pluginLogger.logToolCall("Executed tool: $toolData")

        return Message(
            toolData.toString(),
            Message.TOOL
        )
    }
}
