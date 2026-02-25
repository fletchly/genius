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

package io.fletchly.genius.infrastructure.tool.minecraft

import io.fletchly.genius.infrastructure.scheduling.PluginScheduler
import io.fletchly.genius.infrastructure.tool.Tool
import io.fletchly.genius.infrastructure.tool.ToolResult
import io.fletchly.genius.infrastructure.tool.tool
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module
import java.time.LocalDate

fun gameInfoTool(
    plugin: JavaPlugin,
    pluginScheduler: PluginScheduler
) = tool("game_info") {
    description = "Get information about the Minecraft server and current date. Used to ensure up-to-date information."

    handler {
        val serverVersion = pluginScheduler.runTask { plugin.server.version }
        val currentDate = LocalDate.now().toString()
        val gameInfo = GameInfo(serverVersion, currentDate)

        ToolResult.Success(Json.encodeToJsonElement(gameInfo))
    }
}

@Serializable
data class GameInfo(
    val serverVersion: String,
    val currentDate: String
)

val gameInfoToolModule = module {
    single(named("game_info")) { gameInfoTool(get(), get()) } bind Tool::class
}