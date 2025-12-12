package io.fletchly.genius.command.commands

import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.tree.LiteralCommandNode
import io.fletchly.genius.Genius
import io.fletchly.genius.command.commands.GeniusCommand
import io.fletchly.genius.config.manager.ConfigurationManager
import io.fletchly.genius.conversation.service.ConversationManager
import io.fletchly.genius.ollama.service.ChatServiceException
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import io.papermc.paper.registry.keys.SoundEventKeys
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.entity.Player
import java.util.logging.Logger
import javax.inject.Inject

/**
 * Structure and logic for /ask
 */
class AskCommand @Inject constructor(
    configurationManager: ConfigurationManager,
    private val plugin: Genius,
    private val pluginScope: CoroutineScope,
    private val pluginLogger: Logger,
    private val conversationManager: ConversationManager
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

    private val displayName = Component.text("[")
        .append { Component.text(configurationManager.geniusAgentName, NamedTextColor.GREEN) }
        .append { Component.text("] ") }

    override fun execute(ctx: CommandContext<CommandSourceStack>): Int {
        sendPlayerMessage(ctx)
        generateChatAsync(ctx)
        return com.mojang.brigadier.Command.SINGLE_SUCCESS
    }

    private fun sendPlayerMessage(ctx: CommandContext<CommandSourceStack>) {
        val playerName = ctx.source.executor!!.name // safe to assume not null here because of command requirements
        val prompt = ctx.getArgument("prompt", String::class.java)

        val playerMessage = Component.text {
            it.content("[$playerName] $prompt")
            it.color(NamedTextColor.GRAY)
            it.decoration(TextDecoration.ITALIC, true)
        }

        ctx.source.sender.sendMessage { playerMessage }
    }

    private fun sendResponse(response: String, ctx: CommandContext<CommandSourceStack>) {
        ctx.source.sender.playSound(
            Sound.sound(
                SoundEventKeys.ENTITY_EXPERIENCE_ORB_PICKUP,
                Sound.Source.MASTER,
                1f,
                1f
            ), Sound.Emitter.self()
        )

        ctx.source.sender.sendMessage { displayName.append { Component.text(response) } }
    }

    private fun sendFailure(message: String, ctx: CommandContext<CommandSourceStack>) {
        ctx.source.sender.playSound(
            Sound.sound(
                SoundEventKeys.BLOCK_GLASS_BREAK,
                Sound.Source.MASTER,
                1f,
                1f
            ), Sound.Emitter.self()
        )

        ctx.source.sender.sendMessage { displayName.append { Component.text(message, NamedTextColor.RED) } }
    }

    private fun generateChatAsync(ctx: CommandContext<CommandSourceStack>) {
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