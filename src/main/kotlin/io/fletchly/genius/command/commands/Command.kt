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

package io.fletchly.genius.command.commands

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