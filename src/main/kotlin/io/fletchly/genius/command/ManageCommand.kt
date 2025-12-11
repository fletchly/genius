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

package io.fletchly.genius.command

import com.mojang.brigadier.Command
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.tree.LiteralCommandNode
import io.fletchly.genius.context.service.ContextService
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import java.util.logging.Logger

class ManageCommand @Inject constructor(
    private val contextService: ContextService,
    private val pluginScope: CoroutineScope,
    private val pluginLogger: Logger
) {
    val description = "Manage Genius"

    fun createCommandNode(): LiteralCommandNode<CommandSourceStack> {
        return Commands.literal("genius")
            .requires {
                it.sender.hasPermission("genius.manage")
            }
            .then(
                Commands.literal("clear")
                    .executes { clearContext(it)}
            )
            .build()
    }

    private fun clearContext(ctx: CommandContext<CommandSourceStack>): Int {
        ctx.source.sender.sendMessage(text("Clearing context for all players...", NamedTextColor.YELLOW))

        val job = pluginScope.launch {
            contextService.clearContext()
        }

        job.invokeOnCompletion { throwable ->
            val message = if (throwable == null) {
                "Context cleared for all players!"
            } else {
                "Failed to clear context: ${throwable.message}"
            }
            pluginLogger.info { message }
            ctx.source.sender.sendMessage(text(message, NamedTextColor.YELLOW))
        }

        return Command.SINGLE_SUCCESS
    }
}