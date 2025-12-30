package io.fletchly.genius.command

import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack

/**
 * A single command
 *
 * @property description a short description of the command function
 * @property aliases a list of command aliases
 * @property permission the permission needed to use the command
 * @property commandNode command structure
 */
interface Command {
    val description: String
    val aliases: List<String>
    val permission: String

    val commandNode: LiteralCommandNode<CommandSourceStack>

    /**
     * Command execution logic
     *
     * @return always Command.SINGLE_SUCCESS (1)
     */
    fun execute(ctx: CommandContext<CommandSourceStack>): Int
}