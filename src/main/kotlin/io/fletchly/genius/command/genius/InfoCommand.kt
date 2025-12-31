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
import io.fletchly.genius.util.PluginSchedulerUtil
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import net.kyori.adventure.text.Component
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class InfoCommand(
    private val contextService: ContextService,
    private val pluginScope: CoroutineScope,
    private val chatMessageUtil: ChatMessageUtil,
    private val pluginSchedulerUtil: PluginSchedulerUtil
) : Command {
    override val description = "Get info on Genius"
    override val aliases: List<String> = listOf()
    override val permission = "genius.manage.info"
    override val commandNode: LiteralCommandNode<CommandSourceStack>
        get() {
            return Commands.literal("info")
                .requires {
                    it.sender.hasPermission(permission)
                }
                .executes { execute(it) }
                .build()

        }

    override fun execute(ctx: CommandContext<CommandSourceStack>): Int {
        val executor = ctx.source.executor
        val sender = ctx.source.sender

        if (executor is Player) {
            val playerUuid = executor.uniqueId

            pluginScope.launch {
                val playerContextSize = contextService.getContext(playerUuid).size
                pluginSchedulerUtil.runTask {
                    sendLines(chatMessageUtil.infoMessage(playerContextSize), sender)
                }
            }
            return com.mojang.brigadier.Command.SINGLE_SUCCESS
        }

        sendLines(chatMessageUtil.infoMessage(), sender)
        return com.mojang.brigadier.Command.SINGLE_SUCCESS
    }

    private fun sendLines(lines: List<Component>, sender: CommandSender) {
        for (line in lines) {
            sender.sendMessage(line)
        }
    }
}