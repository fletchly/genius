package io.fletchly.genius.command

import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.tree.LiteralCommandNode
import io.fletchly.genius.command.genius.ClearCommand
import io.fletchly.genius.command.genius.InfoCommand
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands

class GeniusCommand(
    private val clearCommand: ClearCommand,
    private val infoCommand: InfoCommand
) : Command {
    override val description = "Manage Genius"
    override val aliases = listOf<String>()
    override val permission = "genius.manage"
    override val commandNode: LiteralCommandNode<CommandSourceStack>
        get() {
            return Commands.literal("genius")
                .then(infoCommand.commandNode)
                .then(clearCommand.commandNode)
                .build()
        }

    override fun execute(ctx: CommandContext<CommandSourceStack>): Int {
        return com.mojang.brigadier.Command.SINGLE_SUCCESS
    }
}