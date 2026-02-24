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

package io.fletchly.genius.adapter.outbound.ai.ollama.model

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