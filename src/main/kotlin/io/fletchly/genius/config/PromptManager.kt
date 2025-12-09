package io.fletchly.genius.config

import org.bukkit.plugin.java.JavaPlugin
import java.io.File

/**
 * System prompt manager
 */
class PromptManager(private val plugin: JavaPlugin) {
    private var _prompt: String = loadDefaultPrompt()
    val prompt: String get() = _prompt
    private val promptPath = "system-prompt.md"

    init {
        saveDefaultPrompt()
        loadPromptFile()
    }

    /**
     * Load bundled default prompt from resources
     */
    private fun loadDefaultPrompt(): String = plugin.getResource(promptPath)!!
        .bufferedReader()
        .use { it.readText() }

    /**
     * Save default prompt to server
     */
    private fun saveDefaultPrompt() {
        plugin.saveResource(promptPath, false)
    }

    /**
     * Reload prompt
     */
    fun reload() {
        loadPromptFile()
        plugin.logger.info("System prompt reloaded from $promptPath")
    }

    /**
     * Load the contents of the prompt file
     */
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