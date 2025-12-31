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

package io.fletchly.genius.manager.config

import org.spongepowered.configurate.objectmapping.ConfigSerializable
import org.spongepowered.configurate.objectmapping.meta.Comment
import org.spongepowered.configurate.objectmapping.meta.Setting

/**
 * Genius plugin configuration
 */
@ConfigSerializable
data class GeniusConfiguration(
    @Setting("display")
    @Comment("Properties for the agent's appearance and behavior")
    val display: DisplayConfiguration = DisplayConfiguration(),

    @Setting("ollama")
    @Comment("Ollama client configuration")
    val ollama: OllamaConfiguration = OllamaConfiguration(),

    @Setting("context")
    @Comment("Context store configuration")
    val context: ContextConfiguration = ContextConfiguration(),

    @Setting("tool")
    @Comment("Tool configuration")
    val tool: ToolConfiguration = ToolConfiguration(),

    @Setting("logging")
    @Comment("Logging configuration")
    val logging: LoggingConfiguration = LoggingConfiguration(),

    @Setting("version")
    @Comment("Don't change this. Doing so could overwrite existing config")
    val version: Int = 0
) {
    companion object {
        val HEADER = """
            |*****************************************
            |*         Genius Configuration          *
            |*****************************************
            |For reference, see https://fletchly.github.io/genius-wiki/docs/setup/configuration/genius-conf
        """.trimMargin()
    }
}

@ConfigSerializable
data class DisplayConfiguration(
    @Setting("agent-name")
    @Comment("The name used when displaying messages from the agent in chat")
    val agentName: String = "Genius",

    @Setting("agent-prefix")
    @Comment("The prefix used when displaying messages from the agent in chat")
    val agentPrefix: String = "\uD83D\uDCA1",

    @Setting("player-prefix")
    @Comment("The prefix used when displaying messages from players in chat")
    val playerPrefix: String = "\uD83D\uDC64"
)

@ConfigSerializable
data class OllamaConfiguration(
    @Setting("base-url")
    @Comment("The base URL for the Ollama API. For more info, see https://fletchly.github.io/genius-wiki/docs/setup/hosting")
    val baseUrl: String = "https://ollama.com",

    @Setting("api-key")
    @Comment("Your key for the Ollama cloud API. This only needs to be set if you are using an Ollama cloud model.")
    val apiKey: String? = "",

    @Setting("model")
    @Comment("The name of the model to use for response generation.")
    val model: String = "deepseek-v3.1:671b",

    @Setting("temperature")
    @Comment("Controls how random or deterministic the output is.")
    val temperature: Double = 0.5,

    @Setting("top-k")
    @Comment("Restricts sampling to the K most probable next tokens, making output more focused (low values) or more creative (high values).")
    val topK: Int = 40,

    @Setting("top-p")
    @Comment("Limits sampling to the smallest group of likely next tokens that together reach probability P, for more focused (low P) or creative (high P) output.")
    val topP: Double = 0.85,

    @Setting("num-predict")
    @Comment("Sets the maximum number of tokens the model can generate in its response (higher values allow longer outputs; lower values keep them shorter)")
    val numPredict: Int = 400
)

@ConfigSerializable
data class ContextConfiguration(
    @Setting("max-player-messages")
    @Comment("Maximum number of messages per player to store at one time")
    val maxPlayerMessages: Int = 20
)

@ConfigSerializable
data class ToolConfiguration(
    @Setting("web-search")
    @Comment("Web search tool configuration")
    val webSearch: WebSearchConfiguration = WebSearchConfiguration()
)

@ConfigSerializable
data class WebSearchConfiguration(
    @Setting("enabled")
    @Comment("Enable/disable the web search tool. Note: ollama.api-key must be set for this to work, regardless of hosting strategy")
    val enabled: Boolean = true,

    @Setting("truncate-results")
    @Comment("Number of characters to truncate search results to")
    val truncateResults: Int = 8000
)

@ConfigSerializable
data class LoggingConfiguration(
    @Setting("log-http-requests")
    @Comment("Log all HTTP requests made by client to the server console")
    val logHttpRequests: Boolean = false,

    @Setting("log-player-messages")
    @Comment("Log messages between all players and Genius to the server console")
    val logPlayerMessages: Boolean = false,

    @Setting("log-tool-calls")
    @Comment("Log all tool calls to the server console")
    val logToolCalls: Boolean = false
)
