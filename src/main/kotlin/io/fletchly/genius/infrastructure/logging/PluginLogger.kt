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

import io.fletchly.genius.core.model.Target
import io.fletchly.genius.core.port.outbound.LoggingService
import io.fletchly.genius.infrastructure.config.GeniusConfiguration
import io.fletchly.genius.infrastructure.scheduling.PluginScheduler
import org.bukkit.plugin.java.JavaPlugin
import java.util.logging.Logger

/**
 * Plugin logging service
 */
class PluginLogger(
    private val configuration: GeniusConfiguration,
    plugin: JavaPlugin
) : LoggingService {
    private val logger = plugin.logger

    /**
     * Log HTTP requests from client if configured to do so
     *
     * @param request request to log
     */
    fun logHttpRequest(request: String) {
        if (configuration.logging.logHttpRequests) logger.info { request }
    }

    fun logToolCall(message: String) {
        if (configuration.logging.logToolCalls) logger.info { message }
    }

    fun logToolCallError(message: String) {
        if (configuration.logging.logToolCalls) logger.warning { message }
    }

    override fun logInfo(message: String) = logger.info { message }

    /**
     * Log message with warning level
     *
     * @param message message to log
     */
    fun logWarning(message: String) = logger.warning { message }

    /**
     * Log message with severe level
     *
     * @param message message to log
     */
    fun logError(message: String) = logger.severe { message }

    override fun logPlayerMessage(target: Target, message: String) {
        val targetName = target.displayName
        val agentName = configuration.display.agentName

        logConversation("$targetName > $agentName: $message")
    }

    override fun logAssistantMessage(
        target: Target,
        message: String
    ) {
        val targetName = target.displayName
        val agentName = configuration.display.agentName

        logConversation("$agentName > $targetName: $message")
    }

    private fun logConversation(message: String) {
        if (!configuration.logging.logPlayerMessages) return
        logger.info { message }
    }
}