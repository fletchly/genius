package io.fletchly.genius.event.listeners

import io.fletchly.genius.context.service.ContextService
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