/*
 * This file is part of Genius, licensed under the Apache License 2.0
 *
 * Copyright (c) 2025 fletchly
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

package io.fletchly.genius.command.genius

import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.tree.LiteralCommandNode
import io.fletchly.genius.command.Command
import io.fletchly.genius.service.context.ContextService
import io.fletchly.genius.util.ChatMessageUtil
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import io.papermc.paper.command.brigadier.argument.ArgumentTypes
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.bukkit.entity.Player
import java.util.logging.Logger

/**
 * Structure and logic for /manage
 */
class ClearCommand(
    private val contextService: ContextService,
    private val pluginScope: CoroutineScope,
    private val pluginLogger: Logger,
    private val chatMessageUtil: ChatMessageUtil
) : Command {
    override val description = "Clear conversation context"
    override val aliases = listOf<String>()
    override val permission = "genius.manage.clear"
    override val commandNode: LiteralCommandNode<CommandSourceStack>
        get() {
            return Commands.literal("clear")
                .requires {
                    it.sender.hasPermission("$permission.self")
                }
                .executes { execute(it) }
                .then(
                    Commands.argument("targets", ArgumentTypes.players())
                        .requires {
                            it.sender.hasPermission("$permission.any") &&
                                    it.sender.hasPermission("minecraft.command.selector")
                        }
                        .executes { executeAny(it) }
                )
                .build()
        }

    override fun execute(ctx: CommandContext<CommandSourceStack>): Int {
        val executor = ctx.source.executor
        val sender = ctx.source.sender

        if (executor !is Player) {
            sender.sendMessage {
                chatMessageUtil.geniusMessage(
                    ChatMessageUtil.MessageLevel.ERROR,
                    "This command must be run by a player"
                )
            }
            return com.mojang.brigadier.Command.SINGLE_SUCCESS
        }

        val playerUuid = executor.uniqueId

        val job = pluginScope.launch {
            contextService.clearContext(playerUuid)
        }

        job.invokeOnCompletion {
            sender.sendMessage {
                chatMessageUtil.geniusMessage(ChatMessageUtil.MessageLevel.INFO, "Cleared conversation context")
            }
        }

        return com.mojang.brigadier.Command.SINGLE_SUCCESS
    }

    /**
     * Execute for any player
     */
    fun executeAny(ctx: CommandContext<CommandSourceStack>): Int {
        val targetResolver = ctx.getArgument("targets", PlayerSelectorArgumentResolver::class.java)
        val targets = targetResolver.resolve(ctx.source)
        val sender = ctx.source.sender

        val job = pluginScope.launch {
            for (player in targets) {
                contextService.clearContext(player.uniqueId)
            }
        }

        job.invokeOnCompletion {
            val message = "Cleared conversation context for ${targets.size} player${if (targets.size != 1) "s" else ""}"
            pluginLogger.info { message }
            if (sender is Player) sender.sendMessage(
                chatMessageUtil.geniusMessage(
                    ChatMessageUtil.MessageLevel.INFO,
                    message
                )
            )
        }

        return com.mojang.brigadier.Command.SINGLE_SUCCESS
    }
}