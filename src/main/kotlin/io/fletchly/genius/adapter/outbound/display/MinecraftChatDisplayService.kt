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

package io.fletchly.genius.adapter.outbound.display

import io.fletchly.genius.core.port.outbound.DisplayService
import io.fletchly.genius.infrastructure.scheduling.PluginScheduler
import io.fletchly.genius.old.manager.config.GeniusConfiguration
import io.papermc.paper.registry.keys.SoundEventKeys
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import java.util.*

/**
 * [DisplayService] implementation that displays messages in Minecraft chat
 */
class MinecraftChatDisplayService(
    private val configuration: GeniusConfiguration,
    private val pluginScheduler: PluginScheduler
) : DisplayService {
    private val displayConfig = configuration.display

    override suspend fun displayPlayerMessage(playerUUID: UUID, message: String) {
        pluginScheduler.runTask {
            val player = Bukkit.getPlayer(playerUUID) ?: return@runTask

            player.sendMessage { playerMessage(player.name, message) }
        }
    }

    override suspend fun displayAssistantMessage(playerUUID: UUID, message: String) {
        pluginScheduler.runTask {
            val player = Bukkit.getPlayer(playerUUID) ?: return@runTask

            player.playSound(RESPONSE_SOUND, Sound.Emitter.self())
            player.sendMessage { assistantMessage(message) }
        }
    }

    override suspend fun displayErrorMessage(playerUUID: UUID, message: String) {
        pluginScheduler.runTask {
            val player = Bukkit.getPlayer(playerUUID) ?: return@runTask

            player.playSound(ERROR_SOUND, Sound.Emitter.self())
            player.sendMessage { errorMessage(message) }
        }
    }

    private fun playerMessage(playerName: String, message: String) =
        text("${displayConfig.playerPrefix} ", NamedTextColor.AQUA)
            .append { text(playerName, NamedTextColor.WHITE) }
            .append { ARROW_COMPONENT }
            .append { text(message).color(NamedTextColor.GRAY) }

    private fun assistantMessage(message: String) =
        text("${displayConfig.agentPrefix} ", NamedTextColor.YELLOW)
            .append { text(displayConfig.agentName, NamedTextColor.GREEN) }
            .append { ARROW_COMPONENT }
            .append { text(message).color(NamedTextColor.WHITE) }

    private fun errorMessage(message: String) = text(message).color(NamedTextColor.RED)


    private companion object {
        val RESPONSE_SOUND = Sound.sound(
            SoundEventKeys.ENTITY_EXPERIENCE_ORB_PICKUP,
            Sound.Source.MASTER,
            1f,
            1f,
        )

        val ERROR_SOUND = Sound.sound(
            SoundEventKeys.BLOCK_GLASS_BREAK,
            Sound.Source.MASTER,
            1f,
            1f,
        )

        val ARROW_COMPONENT = text( " → ")
    }
}