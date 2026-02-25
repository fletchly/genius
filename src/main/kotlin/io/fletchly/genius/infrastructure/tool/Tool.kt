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

package io.fletchly.genius.infrastructure.tool

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

data class Tool(
    val name: String,
    val description: String,
    val parameters: List<Parameter>,
    val handler: suspend (JsonObject) -> ToolResult
)

sealed interface ToolResult {
    data class Success(val value: JsonElement) : ToolResult
    data class Failure(val message: String) : ToolResult
}

@Serializable
data class ToolData(
    val toolName: String,
    val success: Boolean,
    val data: JsonElement? = null,
    val error: String? = null
) {
    override fun toString(): String = Json.encodeToString(this)
}

class ToolBuilder(private val name: String) {
    var description = ""
    private val parameters = mutableListOf<Parameter>()
    private var handler: (suspend (JsonObject) -> ToolResult)? = null

    fun parameter(name: String, block: ParameterBuilder.() -> Unit) {
        parameters.add(ParameterBuilder(name).apply(block).build())
    }

    fun handler(block: suspend (JsonObject) -> ToolResult) {
        handler = block
    }

    fun build(): Tool {
        requireNotNull(handler) { "Tool '$name' must have a handler defined." }
        return Tool(name, description, parameters, handler!!)
    }
}

fun tool(name: String, block: ToolBuilder.() -> Unit): Tool =
    ToolBuilder(name).apply(block).build()

enum class ParamType { STRING, INTEGER, NUMBER, BOOLEAN, ARRAY, OBJECT }

data class Parameter(
    val name: String,
    val type: ParamType,
    val description: String,
    val required: Boolean = true,
    val enum: List<String>? = null
)

class ParameterBuilder(private val name: String) {
    var type = ParamType.STRING
    var description = ""
    var required = true
    var enum: List<String>? = null

    fun build() = Parameter(name, type, description, required, enum)
}