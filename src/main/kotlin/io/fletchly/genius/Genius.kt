package io.fletchly.genius

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents
import org.bukkit.plugin.java.JavaPlugin

class Genius : JavaPlugin() {
    private lateinit var component: PluginComponent

    override fun onEnable() {
        saveDefaultConfig()
        buildComponent()
        registerCommands()
        logger.info { "Successfully enabled Genius ${pluginMeta.version}!" }
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }

    private fun buildComponent() {
        component = DaggerPluginComponent.builder()
            .pluginModule(PluginModule(this))
            .build()
    }

    private fun registerCommands() {
        logger.info { "Registering commands" }

        lifecycleManager.registerEventHandler(LifecycleEvents.COMMANDS) {
            it.registrar().register(
                component.askCommand().createCommandNode(),
                component.askCommand().description,
                component.askCommand().aliases
            )
        }

        logger.info { "Successfully registered commands" }
    }
}
