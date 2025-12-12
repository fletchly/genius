package io.fletchly.genius.command.commands

import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.tree.LiteralCommandNode
import io.fletchly.genius.context.service.ContextService
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import java.util.logging.Logger
import javax.inject.Inject

/**
 * Structure and logic for /manage
 */
class ManageCommand @Inject constructor(
    private val contextService: ContextService,
    private val pluginScope: CoroutineScope,
    private val pluginLogger: Logger
) : GeniusCommand {
    override val description = "Manage Genius"
    override val aliases = listOf<String>()
    override val permission = "genius.manage"
    override val commandNode: LiteralCommandNode<CommandSourceStack>
        get() {
            return Commands.literal("genius")
                .requires {
                    it.sender.hasPermission("genius.manage")
                }
                .then(
                    Commands.literal("clear")
                        .executes { execute(it) }
                )
                .build()
        }

    override fun execute(ctx: CommandContext<CommandSourceStack>): Int {
        ctx.source.sender.sendMessage(Component.text("Clearing context for all players...", NamedTextColor.YELLOW))

        val job = pluginScope.launch {
            contextService.clearContext()
        }

        job.invokeOnCompletion { throwable ->
            val message = if (throwable == null) {
                "Context cleared for all players!"
            } else {
                "Failed to clear context: ${throwable.message}"
            }
            pluginLogger.info { message }
            ctx.source.sender.sendMessage(Component.text(message, NamedTextColor.YELLOW))
        }

        return com.mojang.brigadier.Command.SINGLE_SUCCESS
    }
}