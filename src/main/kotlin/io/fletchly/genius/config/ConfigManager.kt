/*
 * This file is part of Genius, licensed under the Apache License 2.0.
 *
 * Copyright (c) 2025 fletchly
 * Copyright (c) 2025 contributors
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

package io.fletchly.genius.config

import org.bukkit.configuration.file.FileConfiguration
import javax.inject.Inject

/**
 * Configuration manager
 */
class ConfigManager @Inject constructor(private val config: FileConfiguration) {
    val geniusAgentName: String
        get() = config.getString(
            GENIUS_AGENT_NAME,
            "Genius"
        )!! // Will never be null because default is provided
    val ollamaBaseUrl: String get() = config.getString(OLLAMA_BASE_URL, "http://localhost:11434/")!!
    val ollamaApiKey: String? get() = config.getString(OLLAMA_API_KEY).takeIf { it?.isNotBlank() == true }
    val ollamaModel: String get() = config.getString(OLLAMA_MODEL, "deepseek-v3.1:671b")!!
    val ollamaTemperature: Double get() = config.getDouble(OLLAMA_TEMPERATURE, 0.5)
    val ollamaTopK: Int get() = config.getInt(OLLAMA_TOP_K, 40)
    val ollamaTopP: Double get() = config.getDouble(OLLAMA_TOP_P, 0.85)
    val ollamaNumPredict: Int get() = config.getInt(OLLAMA_NUM_PREDICT, 400)
    val contextMaxPlayerMessages: Int get() = config.getInt(CONTEXT_MAX_PLAYER_MESSAGES, 20)

    /**
     * Path mappings for config file
     */
    private companion object Paths {
        const val GENIUS_AGENT_NAME = "genius.agentName"
        const val OLLAMA_BASE_URL = "ollama.baseUrl"
        const val OLLAMA_API_KEY = "ollama.apiKey"
        const val OLLAMA_MODEL = "ollama.model"
        const val OLLAMA_TEMPERATURE = "ollama.temperature"
        const val OLLAMA_TOP_K = "ollama.topK"
        const val OLLAMA_TOP_P = "ollama.topP"
        const val OLLAMA_NUM_PREDICT = "ollama.numPredict"
        const val CONTEXT_MAX_PLAYER_MESSAGES = "context.maxPlayerMessages"
    }
}