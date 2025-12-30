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

package io.fletchly.genius.util

import io.fletchly.genius.manager.config.GeniusConfiguration
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration

/**
 * Utility for generating formatted chat messages
 */
class ChatMessageUtil(
    configuration: GeniusConfiguration
) {
    enum class MessageLevel(val color: NamedTextColor) {
        INFO(NamedTextColor.GRAY),
        RESPONSE(NamedTextColor.WHITE),
        ERROR(NamedTextColor.RED)
    }

    private val agentName = configuration.display.agentName
    private val agentPrefix = configuration.display.agentPrefix
    private val playerPrefix = configuration.display.playerPrefix
    private val model = configuration.ollama.model
    private val maxPlayerMessages = configuration.context.maxPlayerMessages
    private val agentDisplayName = text("$agentPrefix ", NamedTextColor.YELLOW)
        .append { text(agentName, NamedTextColor.GREEN) }
        .append { text(" → ") }

    private fun playerDisplayName(playerName: String) = text("$playerPrefix ", NamedTextColor.AQUA)
        .append { text(playerName, NamedTextColor.WHITE) }
        .append { text(" → ") }

    /**
     * Build player message chat component
     *
     * @param playerName name of player sending the message
     * @param message message content
     * @return formatted player message
     */
    fun playerMessage(playerName: String, message: String) = playerDisplayName(playerName)
        .append {
            text(message)
                .color(MessageLevel.INFO.color)
        }

    /**
     * Build Genius message chat component
     */
    fun geniusMessage(level: MessageLevel, message: String) = agentDisplayName
        .append {
            text(message)
                .color(level.color)
        }

    fun infoMessage(context: Int? = null): List<Component> {
        val lineOne = text("$agentName info")
            .color(NamedTextColor.GREEN)
            .decoration(TextDecoration.BOLD, true)

        val lineTwo = text("Model: ")
            .append {
                text(model)
                    .color(NamedTextColor.YELLOW)
            }

        val lineThree = text("Context used: ")
            .append {
                text("$context/$maxPlayerMessages")
                    .color(NamedTextColor.YELLOW)
            }

        return if (context != null) {
            listOf(lineOne, lineTwo, lineThree)
        } else {
            listOf(lineOne, lineTwo)
        }
    }
}