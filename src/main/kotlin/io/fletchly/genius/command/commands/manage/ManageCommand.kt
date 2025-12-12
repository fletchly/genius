package io.fletchly.genius.command.commands.manage

import com.mojang.brigadier.Command
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.tree.LiteralCommandNode
import io.fletchly.genius.command.commands.GeniusCommand
import io.fletchly.genius.config.manager.ConfigurationManager
import io.fletchly.genius.context.service.ContextService
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import kotlinx.coroutines.CoroutineScope
import java.util.logging.Logger
import javax.inject.Inject

class ManageCommand @Inject constructor(
    private val clearContextCommand: ClearContextCommand,
    private val infoCommand: InfoCommand
): GeniusCommand {
    override val description = "Manage Genius"
    override val aliases = listOf<String>()
    override val permission = "genius.manage"
    override val commandNode: LiteralCommandNode<CommandSourceStack>
        get() {
            return Commands.literal("genius")
                .then(infoCommand.commandNode)
                .then(clearContextCommand.commandNode)
                .build()
        }

    override fun execute(ctx: CommandContext<CommandSourceStack>): Int {
        return Command.SINGLE_SUCCESS
    }
}