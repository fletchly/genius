package io.fletchly.genius.command

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands

class CommandDsl(literal: String) : LiteralArgumentBuilder<CommandSourceStack>(literal) {
}