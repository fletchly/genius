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

package io.fletchly.genius

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents
import kotlinx.coroutines.*
import org.bukkit.plugin.java.JavaPlugin

class Genius : JavaPlugin() {
    private lateinit var component: PluginComponent
    lateinit var scope: CoroutineScope

    override fun onEnable() {
        saveDefaultConfig()
        registerPluginScope()
        buildComponent()
        registerEvents()
        registerCommands()
        logger.info { "Successfully enabled Genius ${pluginMeta.version}!" }
    }

    override fun onDisable() {
        cleanUpPluginScope()
    }

    private fun buildComponent() {
        component = DaggerPluginComponent.builder()
            .pluginModule(PluginModule(this))
            .build()
    }

    private fun registerPluginScope() {
        scope = CoroutineScope(Dispatchers.Default) + SupervisorJob()
    }

    private fun cleanUpPluginScope() {
        scope.cancel()
    }

    private fun registerEvents() {
        logger.info { "Registering ${component.listeners().size} event listeners" }
        var registered = 0

        for (listener in component.listeners()) {
            server.pluginManager.registerEvents(listener, this)
            registered++
        }

        logger.info { "Successfully registered ${registered}/${component.listeners().size} event listeners" }
    }

    private fun registerCommands() {
        logger.info { "Registering ${component.commands().size} commands" }
        var registered = 0

        for (command in component.commands()) {
            lifecycleManager.registerEventHandler(LifecycleEvents.COMMANDS) {
                it.registrar().register(
                    command.commandNode,
                    command.description,
                    command.aliases
                )
            }
            registered++
        }

        logger.info { "Successfully registered ${registered}/${component.commands().size} commands" }
    }
}
