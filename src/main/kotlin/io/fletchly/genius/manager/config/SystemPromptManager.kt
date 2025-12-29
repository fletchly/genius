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

package io.fletchly.genius.manager.config

import org.bukkit.plugin.java.JavaPlugin
import java.io.File

/**
 * System prompt manager
 *
 * @property prompt Genius system prompt
 */
class SystemPromptManager constructor(private val plugin: JavaPlugin) {
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