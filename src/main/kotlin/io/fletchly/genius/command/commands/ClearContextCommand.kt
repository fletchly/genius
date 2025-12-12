package io.fletchly.genius.command.commands

import com.mojang.brigadier.Command
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.tree.LiteralCommandNode
import io.fletchly.genius.context.service.ContextService
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import io.papermc.paper.command.brigadier.argument.ArgumentTypes
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.entity.Player
import java.util.logging.Logger
import javax.inject.Inject


/**
 * Structure and logic for /manage
 */
class ClearContextCommand @Inject constructor(
    private val contextService: ContextService,
    private val pluginScope: CoroutineScope,
    private val pluginLogger: Logger
) : GeniusCommand {
    override val description = "Clear conversation context"
    override val aliases = listOf<String>()
    override val permission = "genius.clear"
    override val commandNode: LiteralCommandNode<CommandSourceStack>
        get() {
            // TODO: Figure out permissions to allow "clear any" command to be used from the console
            return Commands.literal("genius")
                .then(
                    Commands.literal("clear")
                        .requires {
                            it.executor is Player &&
                            it.sender.hasPermission("$permission.self")
                        }
                        .executes { execute(it) }
                        .then(
                            Commands.argument("targets", ArgumentTypes.players())
                                .requires {
                                    it.sender.hasPermission("$permission.any") &&
                                    it.sender.hasPermission("minecraft.command.selector")
                                }
                                .executes { executeAny(it) }
                        )
                )
                .build()
        }

    override fun execute(ctx: CommandContext<CommandSourceStack>): Int {
        val playerUuid = ctx.source.executor!!.uniqueId // safe to assume not null here because of command requirements

        val job = pluginScope.launch {
            contextService.clearContext(playerUuid)
        }

        job.invokeOnCompletion { throwable ->
            val message = if (throwable == null) {
                "Cleared conversation context"
            } else {
                "Failed to clear context: ${throwable.message}"
            }
            ctx.source.sender.sendMessage(Component.text(message, NamedTextColor.GRAY))
        }

        return Command.SINGLE_SUCCESS
    }

    fun executeAny(ctx: CommandContext<CommandSourceStack>): Int {
        val targetResolver = ctx.getArgument("targets", PlayerSelectorArgumentResolver::class.java)
        val targets = targetResolver.resolve(ctx.source)
        val sender = ctx.source.sender

        val job = pluginScope.launch {
            for(player in targets) {
                contextService.clearContext(player.uniqueId)
            }
        }

        job.invokeOnCompletion { throwable ->
            val message = if (throwable == null) {
                "Cleared conversation context for ${targets.size} player(s)"
            } else {
                "Failed to clear context: ${throwable.message}"
            }
            pluginLogger.info { message }
            sender.sendMessage(Component.text(message, NamedTextColor.GRAY))
        }

        return Command.SINGLE_SUCCESS
    }
}