package io.fletchly.genius.command.commands.manage

import com.mojang.brigadier.Command
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.tree.LiteralCommandNode
import io.fletchly.genius.command.commands.GeniusCommand
import io.fletchly.genius.command.util.ChatMessageUtil
import io.fletchly.genius.context.service.ContextService
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import io.papermc.paper.command.brigadier.argument.ArgumentTypes
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.bukkit.entity.Player
import java.util.logging.Logger
import javax.inject.Inject

/**
 * Structure and logic for /manage
 */
class ClearContextCommand @Inject constructor(
    private val contextService: ContextService,
    private val pluginScope: CoroutineScope,
    private val pluginLogger: Logger,
    private val chatMessageUtil: ChatMessageUtil
) : GeniusCommand {
    override val description = "Clear conversation context"
    override val aliases = listOf<String>()
    override val permission = "genius.manage.clear"
    override val commandNode: LiteralCommandNode<CommandSourceStack>
        get() {
            return Commands.literal("clear")
                .requires {
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
                .build()
        }

    override fun execute(ctx: CommandContext<CommandSourceStack>): Int {
        val executor = ctx.source.executor
        val sender = ctx.source.sender

        if (executor !is Player) {
            sender.sendMessage {
                chatMessageUtil.geniusMessage(
                    ChatMessageUtil.MessageLevel.ERROR,
                    "This command must be run by a player"
                )
            }
            return Command.SINGLE_SUCCESS
        }

        val playerUuid = executor.uniqueId

        val job = pluginScope.launch {
            contextService.clearContext(playerUuid)
        }

        job.invokeOnCompletion {
            sender.sendMessage {
                chatMessageUtil.geniusMessage(ChatMessageUtil.MessageLevel.INFO, "Cleared conversation context")
            }
        }

        return Command.SINGLE_SUCCESS
    }

    /**
     * Execute for any player
     */
    fun executeAny(ctx: CommandContext<CommandSourceStack>): Int {
        val targetResolver = ctx.getArgument("targets", PlayerSelectorArgumentResolver::class.java)
        val targets = targetResolver.resolve(ctx.source)
        val sender = ctx.source.sender

        val job = pluginScope.launch {
            for (player in targets) {
                contextService.clearContext(player.uniqueId)
            }
        }

        job.invokeOnCompletion {
            val message = "Cleared conversation context for ${targets.size} player(s)"
            pluginLogger.info { message }
            sender.sendMessage(chatMessageUtil.geniusMessage(ChatMessageUtil.MessageLevel.INFO, message))
        }

        return Command.SINGLE_SUCCESS
    }
}