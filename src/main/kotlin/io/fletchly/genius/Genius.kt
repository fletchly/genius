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

import io.fletchly.genius.client.clientModule
import io.fletchly.genius.command.Command
import io.fletchly.genius.command.commandModule
import io.fletchly.genius.event.eventModule
import io.fletchly.genius.manager.managerModule
import io.fletchly.genius.service.serviceModule
import io.fletchly.genius.util.utilModule
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents
import kotlinx.coroutines.*
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.java.KoinJavaComponent.getKoin

class Genius : JavaPlugin() {
    lateinit var scope: CoroutineScope

    override fun onEnable() {
        registerPluginScope()
        registerModules()
        registerEvents()
        registerCommands()
        logger.info { "Successfully enabled Genius ${pluginMeta.version}!" }
    }

    override fun onDisable() {
        cleanUpPluginScope()
        stopKoin()
    }

    private fun registerModules() {
        startKoin {
            modules(
                pluginModule(this@Genius),
                clientModule,
                commandModule,
                eventModule,
                managerModule,
                serviceModule,
                utilModule
            )
        }
    }

    private fun registerPluginScope() {
        scope = CoroutineScope(Dispatchers.Default) + SupervisorJob()
    }

    private fun cleanUpPluginScope() {
        scope.cancel()
    }

    private fun registerEvents() {
        val listeners = getKoin().getAll<Listener>()

        logger.info { "Registering ${listeners.size} event listeners" }
        var registered = 0

        for (listener in listeners) {
            server.pluginManager.registerEvents(listener, this)
            registered++
        }

        logger.info { "Successfully registered ${registered}/${listeners.size} event listeners" }
    }

    private fun registerCommands() {
        val commands = getKoin().getAll<Command>()

        logger.info { "Registering ${commands.size} commands" }
        var registered = 0

        for (command in commands) {
            lifecycleManager.registerEventHandler(LifecycleEvents.COMMANDS) {
                it.registrar().register(
                    command.commandNode,
                    command.description,
                    command.aliases
                )
            }
            registered++
        }

        logger.info { "Successfully registered ${registered}/${commands.size} commands" }
    }
}
