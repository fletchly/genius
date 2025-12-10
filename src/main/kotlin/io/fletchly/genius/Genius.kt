package io.fletchly.genius

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.plus
import org.bukkit.plugin.java.JavaPlugin

class Genius : JavaPlugin() {
    private lateinit var component: PluginComponent
    lateinit var scope: CoroutineScope

    override fun onEnable() {
        saveDefaultConfig()
        registerPluginScope()
        buildComponent()
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
