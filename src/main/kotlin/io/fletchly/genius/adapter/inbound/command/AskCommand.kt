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

import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.tree.LiteralCommandNode
import io.fletchly.genius.core.port.inbound.GenerateAssistantResponse
import io.fletchly.genius.infrastructure.scheduling.PluginScheduler
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import org.bukkit.entity.Player

class AskCommand(
    generateAssistantResponse: GenerateAssistantResponse,
    pluginScheduler: PluginScheduler
) : Command {
    override val definition = command {
        description = "Ask genius a question"
        permission = "genius.ask"
        aliases("g")
        handle { ctx ->
            // Safe to assume that executor is a non-null
            // player here due to command requirements
            val playerUUID = ctx.source.executor!!.uniqueId
            val prompt = ctx.getArgument("prompt", String::class.java)

            pluginScheduler.runCoroutine {
                generateAssistantResponse.handlePlayerInput(playerUUID, prompt)
            }

            com.mojang.brigadier.Command.SINGLE_SUCCESS
        }
    }
    override val structure: LiteralCommandNode<CommandSourceStack> = Commands.literal("ask")
        .requires {
            it.executor is Player && it.sender.hasPermission(definition.permission)
        }
        .then(
            Commands.argument("prompt", StringArgumentType.greedyString())
                .executes {
                    definition.handler(it)
                }
        )
        .build()
}