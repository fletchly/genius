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

package io.fletchly.genius

import io.fletchly.genius.adapter.inbound.command.GeniusCommand
import io.fletchly.genius.adapter.inbound.command.registerCommand
import io.fletchly.genius.adapter.inbound.event.registerEventListener
import io.fletchly.genius.core.port.outbound.ContextService
import io.fletchly.genius.infrastructure.di.pluginModule
import io.fletchly.genius.infrastructure.scheduling.PluginScheduler
import kotlinx.coroutines.runBlocking
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.java.KoinJavaComponent.getKoin

class Genius : JavaPlugin() {
    override fun onEnable() {
        startKoin { modules( pluginModule(this@Genius) ) }
        registerCommands()
        registerEventListeners()

        logger.info { "Successfully enabled Genius ${pluginMeta.version}. Happy chatting! \uD83D\uDCA1" }
    }

    override fun onDisable() {
        val contextService = getKoin().get<ContextService>()
        val pluginScheduler = getKoin().get<PluginScheduler>()

        logger.info { "Clearing context for all users..." }
        runBlocking {
            contextService.clearContext()
        }

        pluginScheduler.cancel()

        stopKoin()
    }

    private fun registerCommands() {
        val commands = getKoin().getAll<GeniusCommand>()

        var registered = 0
        commands.forEach {
            registerCommand(it)
            registered ++
        }

        logger.info { "Registered $registered commands" }
    }

    private fun registerEventListeners() {
        val eventListeners = getKoin().getAll<Listener>()

        var registered = 0
        eventListeners.forEach {
            registerEventListener(it)
            registered ++
        }

        logger.info { "Registered $registered event listeners" }
    }
}