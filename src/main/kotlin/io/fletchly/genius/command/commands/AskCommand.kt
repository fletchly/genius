package io.fletchly.genius.command.commands

import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.tree.LiteralCommandNode
import io.fletchly.genius.command.util.ChatMessageUtil
import io.fletchly.genius.command.util.PluginSchedulerUtil
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
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.logging.Logger
import javax.inject.Inject

/**
 * Structure and logic for /ask
 */
class AskCommand @Inject constructor(
    configurationManager: ConfigurationManager,
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

        val playerMessage = Component.text {
            it.content("[$playerName] $prompt")
            it.color(NamedTextColor.GRAY)
            it.decoration(TextDecoration.ITALIC, true)
        }

        ctx.source.sender.sendMessage { playerMessage }
    }

    private fun chat(ctx: CommandContext<CommandSourceStack>) {
        val prompt = ctx.getArgument("prompt", String::class.java)
        val playerUUID = ctx.source.executor!!.uniqueId // safe to assume not null here because of command requirements
        val sender = ctx.source.sender

        fun sendSuccess(message: String) {
            pluginSchedulerUtil.runTask {
                playSuccessSound(sender)
                sender.sendMessage {
                    chatMessageUtil.geniusMessage(ChatMessageUtil.MessageLevel.RESPONSE, message)
                }
            }
        }

        fun sendException(ex: Exception, chatMessage: String = ex.message ?: "No error message available") {
            pluginLogger.warning { ex.message }
            pluginSchedulerUtil.runTask {
                playFailureSound(sender)
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
                val response = conversationManager.generateChat(prompt, playerUUID)
                sendSuccess(response)
            } catch (ex: ChatServiceException) {
                sendException(ex, "An error occurred while generating a response")
            } catch (ex: Exception) {
                pluginLogger.warning { ex.message }
                sendException(ex, "An unknown error occurred")
            }
        }
    }

    private fun playFailureSound(sender: CommandSender) {
        sender.playSound(
            Sound.sound(
                SoundEventKeys.BLOCK_GLASS_BREAK,
                Sound.Source.MASTER,
                1f,
                1f
            ), Sound.Emitter.self()
        )
    }

    private fun playSuccessSound(sender: CommandSender) {
        sender.playSound(
            Sound.sound(
                SoundEventKeys.ENTITY_EXPERIENCE_ORB_PICKUP,
                Sound.Source.MASTER,
                1f,
                1f
            ), Sound.Emitter.self()
        )
    }
}