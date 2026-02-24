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

package io.fletchly.genius.adapter.inbound.event

import io.fletchly.genius.core.port.inbound.ManageContext
import io.fletchly.genius.infrastructure.scheduling.PluginScheduler
import io.fletchly.genius.infrastructure.target.PlayerTarget
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent

class PlayerEvents(
    private val manageContext: ManageContext,
    private val pluginScheduler: PluginScheduler
): Listener {

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        val target = PlayerTarget(event.player)

        pluginScheduler.runCoroutine {
            manageContext.clearContext(target, target.uniqueId)
        }
    }
}