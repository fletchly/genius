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

fun ToolDefinition.toOllamaTool(): OllamaTool {
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
