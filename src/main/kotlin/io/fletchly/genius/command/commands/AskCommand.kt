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

import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.tree.LiteralCommandNode
import io.fletchly.genius.command.util.ChatMessageUtil
import io.fletchly.genius.command.util.PluginSchedulerUtil
import io.fletchly.genius.conversation.service.ConversationManager
import io.fletchly.genius.conversation.service.ConversationManagerException
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import io.papermc.paper.registry.keys.SoundEventKeys
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import net.kyori.adventure.sound.Sound
import org.bukkit.entity.Player
import java.util.logging.Logger
import javax.inject.Inject

/**
 * Structure and logic for /ask
 */
class AskCommand @Inject constructor(
    private val pluginSchedulerUtil: PluginSchedulerUtil,
    private val pluginScope: CoroutineScope,
    private val pluginLogger: Logger,
    private val conversationManager: ConversationManager,
    private val chatMessageUtil: ChatMessageUtil
) : GeniusCommand {
    override val description = "Ask Genius a question"
    override val aliases = listOf("g")
    override val permission = "genius.ask"
    override val commandNode: LiteralCommandNode<CommandSourceStack>
        get() {
            return Commands.literal("ask").requires {
                it.executor is Player && it.sender.hasPermission(permission)
            }.then(
                Commands.argument("prompt", StringArgumentType.greedyString()).executes {
                    execute(it)
                }).build()
        }

    override fun execute(ctx: CommandContext<CommandSourceStack>): Int {
        playerMessage(ctx)
        chat(ctx)
        return com.mojang.brigadier.Command.SINGLE_SUCCESS
    }

    private fun playerMessage(ctx: CommandContext<CommandSourceStack>) {
        val playerName = ctx.source.executor!!.name // safe to assume not null here because of command requirements
        val prompt = ctx.getArgument("prompt", String::class.java)

        ctx.source.sender.sendMessage { chatMessageUtil.playerMessage(playerName, prompt) }
    }

    private fun chat(ctx: CommandContext<CommandSourceStack>) {
        val prompt = ctx.getArgument("prompt", String::class.java)
        val playerUUID = ctx.source.executor!!.uniqueId // safe to assume not null here because of command requirements
        val sender = ctx.source.sender

        fun playFailureSound() {
            sender.playSound(
                Sound.sound(
                    SoundEventKeys.BLOCK_GLASS_BREAK,
                    Sound.Source.MASTER,
                    1f,
                    1f
                ), Sound.Emitter.self()
            )
        }

        fun playSuccessSound() {
            sender.playSound(
                Sound.sound(
                    SoundEventKeys.ENTITY_EXPERIENCE_ORB_PICKUP,
                    Sound.Source.MASTER,
                    1f,
                    1f
                ), Sound.Emitter.self()
            )
        }

        fun sendSuccess(message: String) {
            pluginSchedulerUtil.runTask { // Need to use the plugin scheduler here to safely touch Bukkit API
                playSuccessSound()
                sender.sendMessage {
                    chatMessageUtil.geniusMessage(ChatMessageUtil.MessageLevel.RESPONSE, message)
                }
            }
        }

        fun sendException(ex: Exception, chatMessage: String = ex.message ?: "No error message available") {
            pluginLogger.warning { ex.message }
            pluginSchedulerUtil.runTask { // Need to use the plugin scheduler here to safely touch Bukkit API
                playFailureSound()
                sender.sendMessage {
                    chatMessageUtil.geniusMessage(ChatMessageUtil.MessageLevel.ERROR, chatMessage)
                }
            }
        }

        sender.sendMessage {
            chatMessageUtil.geniusMessage(ChatMessageUtil.MessageLevel.INFO, "Generating response...")
        }

        pluginScope.launch {
            try {
                val response = conversationManager.chat(prompt, playerUUID)
                sendSuccess(response)
            } catch (e: ConversationManagerException) {
                sendException(e, "Could not generate response: ${e.message}")
            } catch (e: Exception) {
                pluginLogger.warning { e.message }
                sendException(e, "An unknown error occurred")
            }
        }
    }
}