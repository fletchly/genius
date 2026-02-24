/*
 * This file is part of Genius, licensed under the Apache License 2.0
 *
 * Copyright (c) 2026 fletchly
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

package io.fletchly.genius.adapter.inbound.command

import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.tree.LiteralCommandNode
import net.minecraft.commands.CommandSourceStack

interface Command {
    val definition: CommandDefinition
    val structure: LiteralCommandNode<CommandSourceStack>
}

data class CommandDefinition(
    val description: String,
    val permission: String,
    val aliases: List<String>,
    val handler: (CommandContext<CommandSourceStack>) -> Int
)

class CommandBuilder {
    var description: String = ""
    var permission: String = ""
    private val aliases: MutableList<String> = mutableListOf()
    private var handler: (CommandContext<CommandSourceStack>) -> Int = { 0 }

    fun aliases(vararg aliases: String) {
        for (alias in aliases) {
            this.aliases.add(alias)
        }
    }

    fun handle(block: (CommandContext<CommandSourceStack>) -> Int) {
        handler = block
    }

    fun build() = CommandDefinition(description, permission, aliases, handler)
}

fun command(block: CommandBuilder.() -> Unit): CommandDefinition {
    return CommandBuilder().apply(block).build()
}