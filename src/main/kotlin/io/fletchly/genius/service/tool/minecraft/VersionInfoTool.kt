/*
 * This file is part of Genius, licensed under the Apache License 2.0
 *
 * Copyright (c) 2025 fletchly
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

package io.fletchly.genius.service.tool.minecraft

import io.fletchly.genius.service.tool.Tool
import io.fletchly.genius.service.tool.tool
import io.fletchly.genius.util.PluginSchedulerUtil
import io.ktor.http.content.VersionListProperty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.future.await
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import java.time.LocalDate
import java.util.logging.Logger

class GameInfoTool(
    private val plugin: JavaPlugin,
    private val pluginSchedulerUtil: PluginSchedulerUtil,
    private val pluginScope: CoroutineScope
) : Tool {
    override val definition = tool {
        name = "version_info"
        description = "Get information on the current server version and current date"

        handle { args ->
            handleTool(args)
        }
    }

    override suspend fun handleTool(args: JsonObject): String {
        val version: String = pluginSchedulerUtil.callSync {
            plugin.server.version
        }.await()

        val date = LocalDate.now()

        return VersionInfo(version, date.toString()).toString()
    }
}

val gameInfoModule = module {
    singleOf(::GameInfoTool) { bind<Tool>() }
}

@Serializable
data class VersionInfo(
    val version: String,
    val currentDate: String,
) {
    override fun toString(): String {
        return Json.encodeToString(this)
    }
}