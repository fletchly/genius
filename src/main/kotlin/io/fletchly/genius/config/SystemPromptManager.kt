package io.fletchly.genius.config

import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import javax.inject.Inject

/**
 * System prompt manager
 *
 * @property prompt Genius system prompt
 */
class SystemPromptManager @Inject constructor(private val plugin: JavaPlugin) {
    private val promptPath = "system-prompt.md"
    private var _prompt: String = loadDefaultPrompt()
    val prompt: String get() = _prompt

    init {
        saveDefaultPrompt()
        loadPromptFile()
    }

    private fun loadDefaultPrompt(): String = plugin.getResource(promptPath)!!
        .bufferedReader()
        .use { it.readText() }


    private fun saveDefaultPrompt() {
        val file = File(plugin.dataFolder, promptPath)
        if (!file.exists()) {
            plugin.saveResource(promptPath, false)
        }
    }

    /**
     * Reload system prompt from file
     */
    fun reload() {
        loadPromptFile()
        plugin.logger.info("System prompt reloaded from $promptPath")
    }

    private fun loadPromptFile() {
        val file = File(plugin.dataFolder, promptPath)
        _prompt = if (file.exists()) {
            file.readText(Charsets.UTF_8)
        } else {
            plugin.logger.warning("$promptPath not found! → using built-in default")
            loadDefaultPrompt()
        }
    }
}