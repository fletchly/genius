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

package io.fletchly.genius.event.listeners

import io.fletchly.genius.service.context.service.ContextService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import java.util.logging.Logger
import javax.inject.Inject

/**
 * Listener for player events
 */
class PlayerListener @Inject constructor(
    private val pluginLogger: Logger,
    private val pluginScope: CoroutineScope,
    private val contextService: ContextService
) : Listener {
    /**
     * Player quit event handler
     */
    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        val player = event.player
        pluginLogger.info { "Clearing context for ${player.name}" }
        pluginScope.launch {
            contextService.clearContext(player.uniqueId)
        }
    }
}