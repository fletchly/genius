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