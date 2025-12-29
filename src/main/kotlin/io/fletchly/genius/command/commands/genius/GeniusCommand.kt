/*
 * This file is part of Genius, licensed under the Apache License 2.0.
 *
 * Copyright (c) 2025 fletchly
 * Copyright (c) 2025 contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.fletchly.genius.command.commands.genius

import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.tree.LiteralCommandNode
import io.fletchly.genius.command.commands.Command
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