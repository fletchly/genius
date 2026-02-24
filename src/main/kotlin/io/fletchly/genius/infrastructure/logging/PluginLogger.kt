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

package io.fletchly.genius.infrastructure.logging

import io.fletchly.genius.core.port.outbound.LoggingService
import io.fletchly.genius.infrastructure.config.GeniusConfiguration
import io.fletchly.genius.infrastructure.scheduling.PluginScheduler
import org.bukkit.Bukkit
import java.util.*
import java.util.logging.Logger

class PluginLogger(
    private val logger: Logger,
    private val pluginScheduler: PluginScheduler,
    private val configuration: GeniusConfiguration
) : LoggingService {
    fun logHttpRequest(request: String) {
        if (configuration.logging.logHttpRequests) logger.info { request }
    }

    fun logInfo(message: String) = logger.info { message }
    fun logWarning(message: String) = logger.warning { message }
    fun logError(message: String) = logger.severe { message }

    override suspend fun logPlayerMessage(playerUUID: UUID, message: String) {
        pluginScheduler.runTask {
            val player = Bukkit.getPlayer(playerUUID)
            val playerName = player?.name ?: playerUUID.toString()
            val agentName = configuration.display.agentName

            logConversation("$playerName > $agentName: $message")
        }
    }

    override suspend fun logAssistantMessage(
        playerUUID: UUID,
        message: String
    ) {
        pluginScheduler.runTask {
            val player = Bukkit.getPlayer(playerUUID)
            val playerName = player?.name ?: playerUUID.toString()
            val agentName = configuration.display.agentName

            logConversation("$agentName > $playerName: $message")
        }
    }

    private fun logConversation(message: String) {
        if (!configuration.logging.logPlayerMessages) return
        logger.info { message }
    }
}