package io.fletchly.genius.config

import org.bukkit.configuration.file.FileConfiguration

sealed class RawConfig(private val config: FileConfiguration) {
    val geniusAgentName by lazy { config.getString(GENIUS_AGENT_NAME) }
    val geniusSystemPrompt by lazy { config.getString(GENIUS_SYSTEM_PROMPT) }
    val ollamaBaseUrl by lazy { config.getString(OLLAMA_BASE_URL) }
    val ollamaApiKey by lazy { config.getString(OLLAMA_API_KEY) }
    val ollamaModel by lazy { config.getString(OLLAMA_MODEL) }
    val ollamaTemperature by lazy { config.getDouble(OLLAMA_TEMPERATURE) }
    val ollamaTopK by lazy { config.getInt(OLLAMA_TOP_K) }
    val ollamaTopP by lazy { config.getDouble(OLLAMA_TOP_P) }
    val ollamaNumPredict by lazy { config.getInt(OLLAMA_NUM_PREDICT) }
    val contextMaxPlayerMessages by lazy { config.getInt(CONTEXT_MAX_PLAYER_MESSAGES) }

    private companion object Paths {
        const val GENIUS_AGENT_NAME = "genius.agentName"
        const val GENIUS_SYSTEM_PROMPT = "genius.systemPrompt"
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