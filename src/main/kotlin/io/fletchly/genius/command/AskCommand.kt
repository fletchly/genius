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
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.tree.LiteralCommandNode
import io.fletchly.genius.Genius
import io.fletchly.genius.config.ConfigManager
import io.fletchly.genius.conversation.service.ConversationManager
import io.fletchly.genius.ollama.service.ChatServiceException
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import io.papermc.paper.registry.keys.SoundEventKeys
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.entity.Player
import java.util.logging.Logger
import javax.inject.Inject

class AskCommand @Inject constructor(
    configManager: ConfigManager,
    private val plugin: Genius,
    private val pluginScope: CoroutineScope,
    private val pluginLogger: Logger,
    private val conversationManager: ConversationManager
) {
    val description = "Ask genius a question"
    val aliases = listOf("g")

    private val agentName = configManager.geniusAgentName

    private val displayName = text("[")
        .append { text(agentName, NamedTextColor.GREEN) }
        .append { text("] ") }

    /**
     * Creates command structure
     */
    fun createCommandNode(): LiteralCommandNode<CommandSourceStack> {
        return Commands.literal("ask")
            .requires {
                it.executor is Player &&
                        it.sender.hasPermission("genius.ask")
            }
            .then(
                Commands.argument("prompt", StringArgumentType.greedyString())
                    .executes {
                        generateChat(it)
                    }
            )
            .build()
    }

    /**
     * Executes chat generation logic
     *
     * @param ctx command context
     */
    private fun generateChat(ctx: CommandContext<CommandSourceStack>): Int {
        sendPlayerMessage(ctx)
        executeAsync(ctx)
        return Command.SINGLE_SUCCESS
    }

    /**
     * Displays player message in chat
     *
     * @param ctx command context
     */
    private fun sendPlayerMessage(ctx: CommandContext<CommandSourceStack>) {
        val playerName = ctx.source.executor!!.name // safe to assume not null here because of command requirements
        val prompt = ctx.getArgument("prompt", String::class.java)

        val playerMessage = text {
            it.content("[$playerName] $prompt")
            it.color(NamedTextColor.GRAY)
            it.decoration(TextDecoration.ITALIC, true)
        }

        ctx.source.sender.sendMessage { playerMessage }
    }

    /**
     * Displays response in chat
     *
     * @param response message to send to player
     * @param ctx command context
     */
    private fun sendResponse(response: String, ctx: CommandContext<CommandSourceStack>) {
        ctx.source.sender.playSound(
            Sound.sound(
                SoundEventKeys.ENTITY_EXPERIENCE_ORB_PICKUP,
                Sound.Source.MASTER,
                1f,
                1f
            ), Sound.Emitter.self()
        )

        ctx.source.sender.sendMessage { displayName.append { text(response) } }
    }

    /**
     * Displays error message in chat
     *
     * @param message error message
     */
    private fun sendFailure(message: String, ctx: CommandContext<CommandSourceStack>) {
        ctx.source.sender.playSound(
            Sound.sound(
                SoundEventKeys.BLOCK_GLASS_BREAK,
                Sound.Source.MASTER,
                1f,
                1f
            ), Sound.Emitter.self()
        )

        ctx.source.sender.sendMessage { displayName.append { text(message, NamedTextColor.RED) } }
    }

    /**
     * Executes core command logic asynchronously
     *
     * @param ctx command context
     */
    private fun executeAsync(ctx: CommandContext<CommandSourceStack>) {
        val prompt = ctx.getArgument("prompt", String::class.java)
        val playerUUID = ctx.source.executor!!.uniqueId // safe to assume not null here because of command requirements

        pluginScope.launch {
            try {
                val response = conversationManager.generateChat(prompt, playerUUID)
                // Use plugin scheduler to safely access chat API from coroutine context
                plugin.server.scheduler.runTask(plugin, Runnable {
                    sendResponse(response, ctx)
                })
            } catch (chatServiceEx: ChatServiceException) {
                pluginLogger.warning { chatServiceEx.message }
                plugin.server.scheduler.runTask(plugin, Runnable {
                    sendFailure("An error occurred while generating a response", ctx)
                })

            } catch (ex: Exception) {
                pluginLogger.warning { ex.message }
                plugin.server.scheduler.runTask(plugin, Runnable {
                    sendFailure("An unknown error occurred", ctx)
                })
            }
        }
    }
}