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

    fun createCommandNode(): LiteralCommandNode<CommandSourceStack> {
        return Commands.literal("ask")
            .requires {
                it.executor is Player &&
                it.sender.hasPermission("genius.ask")
            }
            .then(
                Commands.argument("prompt", StringArgumentType.greedyString())
                    .executes {
                        execute(it)
                    }
            )
            .build()
    }

    private fun execute(ctx: CommandContext<CommandSourceStack>): Int {
        sendPlayerMessage(ctx)
        executeAsync(ctx)
        return Command.SINGLE_SUCCESS
    }

    /**
     * Display player prompt in chat
     */
    private fun sendPlayerMessage(ctx: CommandContext<CommandSourceStack>) {
        // Get required values from command context
        val playerName = ctx.source.executor!!.name // safe to assume not null here because of command requirements
        val prompt = ctx.getArgument("prompt", String::class.java)

        // Build message
        val playerMessage = text {
            it.content("[$playerName] $prompt")
            it.color(NamedTextColor.GRAY)
            it.decoration(TextDecoration.ITALIC, true)
        }

        // Display message in chat
        ctx.source.sender.sendMessage { playerMessage }
    }

    /**
     * Display Genius response in chat
     */
    private fun sendResponse(response: String, ctx: CommandContext<CommandSourceStack>) {
        // Play success sound
        ctx.source.sender.playSound(
            Sound.sound(
                SoundEventKeys.ENTITY_EXPERIENCE_ORB_PICKUP,
                Sound.Source.MASTER,
                1f,
                1f
            ), Sound.Emitter.self()
        )

        // Display response in chat
        ctx.source.sender.sendMessage { displayName.append { text(response) } }
    }

    /**
     * Display error messages in chat
     */
    private fun sendFailure(message: String, ctx: CommandContext<CommandSourceStack>) {
        // Play failure sound
        ctx.source.sender.playSound(
            Sound.sound(
                SoundEventKeys.BLOCK_GLASS_BREAK,
                Sound.Source.MASTER,
                1f,
                1f
            ), Sound.Emitter.self()
        )

        // Display response in chat
        ctx.source.sender.sendMessage { displayName.append { text(message, NamedTextColor.RED) }  }
    }

    private fun executeAsync(ctx: CommandContext<CommandSourceStack>) {
        val prompt = ctx.getArgument("prompt", String::class.java)
        val playerUUID = ctx.source.executor!!.uniqueId // safe to assume not null here because of command requirements

        pluginScope.launch {
            try {
                val response = conversationManager.generateChat(prompt, playerUUID)
                plugin.server.scheduler.runTask(plugin, Runnable {
                    sendResponse(response, ctx)
                })
            } catch (chatServiceEx: ChatServiceException) {
                pluginLogger.warning { chatServiceEx.message }
                sendFailure("An error occurred while generating a response", ctx)

            } catch (ex: Exception) {
                pluginLogger.warning { ex.message }
                sendFailure("An unknown error occurred", ctx)
            }
        }
    }
}