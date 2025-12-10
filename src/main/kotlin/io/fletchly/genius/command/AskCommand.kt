package io.fletchly.genius.command

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.tree.LiteralCommandNode
import io.fletchly.genius.config.ConfigManager
import io.fletchly.genius.conversation.service.ConversationManager
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor.GREEN;
import org.bukkit.entity.Player
import javax.inject.Inject

class AskCommand @Inject constructor(configManager: ConfigManager) {
    val description = "Ask genius a question"
    val aliases = listOf("g")

    private val displayName = text("[")
            .append {
                text(configManager.geniusAgentName, GREEN)
                text("] ")
            }

    fun createCommandNode(): LiteralCommandNode<CommandSourceStack> {
        return Commands.literal("ask")
            .requires {
                it.executor is Player &&
                it.sender.hasPermission("genius.ask")
            }
            .then(
                Commands.argument("prompt", StringArgumentType.greedyString())
            )
            .executes {
                execute(it)
            }
            .build()
    }

    private fun execute(ctx: CommandContext<CommandSourceStack>): Int {
        val prompt = ctx.getArgument("prompt", String::class.java)
        val playerUUID = ctx.source.executor!!.uniqueId // safe to assume not null here because of command requirements

        ctx.source.sender.sendMessage { text("$playerUUID -> '$prompt'") }
        return Command.SINGLE_SUCCESS
    }
}