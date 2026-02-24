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

package io.fletchly.genius.adapter.outbound.tool.minecraft

import io.fletchly.genius.infrastructure.scheduling.PluginScheduler
import io.fletchly.genius.infrastructure.tool.Tool
import io.fletchly.genius.infrastructure.tool.tool
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import org.bukkit.plugin.java.JavaPlugin
import java.time.LocalDate

class GameInfoTool(
    private val plugin: JavaPlugin,
    private val pluginScheduler: PluginScheduler,
) : Tool {
    override val definition = tool {
        name = "game_info"
        description = "Get the current Minecraft server version, as well as the current date"
        handle { args ->
            handleTool(args)
        }
    }

    override suspend fun handleTool(args: JsonObject): String {
        val version = pluginScheduler.runTask {
            plugin.server.version
        }

        val currentDate = LocalDate.now().toString()

        return GameInfo(version, currentDate).toString()
    }

}

@Serializable
data class GameInfo(
    val version: String,
    val currentDate: String
) {
    override fun toString(): String {
        return Json.encodeToString(this)
    }
}