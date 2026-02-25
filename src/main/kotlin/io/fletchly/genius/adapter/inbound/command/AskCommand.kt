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

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.StringArgumentType
import io.fletchly.genius.core.port.inbound.GenerateAssistantResponse
import io.fletchly.genius.infrastructure.scheduling.PluginScheduler
import io.fletchly.genius.infrastructure.target.ConsoleTarget
import io.fletchly.genius.infrastructure.target.PlayerTarget
import io.papermc.paper.command.brigadier.Commands
import org.bukkit.entity.Player
import org.bukkit.permissions.PermissionDefault

fun askCommand(
    generateAssistantResponse: GenerateAssistantResponse,
    pluginScheduler: PluginScheduler,
) = command("ask") {
    description = "Ask Genius a question"
    aliases = listOf("g")
    permission = "genius.ask"
    permissionDescription = "Allows a player to ask Genius questions"
    permissionDefault = PermissionDefault.TRUE

    node {
        then(
            Commands.argument("prompt", StringArgumentType.greedyString())
                .executes { ctx ->
                    val prompt = StringArgumentType.getString(ctx, "prompt")
                    val target = when (val sender = ctx.source.sender) {
                        is Player -> PlayerTarget(sender)
                        else -> ConsoleTarget
                    }

                    pluginScheduler.runCoroutine {
                        generateAssistantResponse.handleInput(target, prompt)
                    }

                    Command.SINGLE_SUCCESS
                }
        )
    }
}