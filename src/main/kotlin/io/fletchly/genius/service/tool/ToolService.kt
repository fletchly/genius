/*
 * This file is part of Genius, licensed under the Apache License 2.0
 *
 * Copyright (c) 2025 fletchly
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

package io.fletchly.genius.service.tool

import io.fletchly.genius.manager.config.GeniusConfiguration
import io.fletchly.genius.model.Message
import io.fletchly.genius.model.ToolCall
import java.util.logging.Logger

class ToolService(
    private val registry: ToolRegistry,
    private val configuration: GeniusConfiguration,
    private val logger: Logger,
    tools: Set<Tool>
) {
    init {
        for (tool in tools) {
            registry.register(tool.definition)
        }
    }

    suspend fun executeToolCall(toolCall: ToolCall): Message {
        registry.execute(toolCall.function.name, toolCall.function.arguments)
            .onSuccess {
                if (configuration.logging.logToolCalls) logger.info { "Executed tool: ${toolCall.function.name} > $it" }
                return Message(
                    content = it,
                    role = Message.TOOL
                )
            }
            .onFailure {
                if (configuration.logging.logToolCalls) logger.warning { "Attempted to execute nonexistent tool: ${toolCall.function.name}" }
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
}