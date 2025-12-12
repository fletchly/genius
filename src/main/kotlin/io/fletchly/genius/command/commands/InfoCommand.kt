package io.fletchly.genius.command.commands

import com.mojang.brigadier.Command
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.tree.LiteralCommandNode
import io.fletchly.genius.config.manager.ConfigurationManager
import io.fletchly.genius.context.service.ContextService
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.UUID
import javax.inject.Inject
import kotlin.uuid.Uuid

class InfoCommand @Inject constructor(
    private val configurationManager: ConfigurationManager,
    private val contextService: ContextService,
    private val pluginScope: CoroutineScope
): GeniusCommand {
    override val description = "Get info on Genius"
    override val aliases: List<String> = listOf()
    override val permission = "genius.info"
    override val commandNode: LiteralCommandNode<CommandSourceStack>
        get() {
            return Commands.literal("genius")
                .then(
                    Commands.literal("info")
                        .requires {
                            it.sender.hasPermission(permission)
                        }
                        .executes { execute(it) }
                )
                .build()
        }

    override fun execute(ctx: CommandContext<CommandSourceStack>): Int {
        val agentName = configurationManager.geniusAgentName
        val model = configurationManager.ollamaModel
        val executor = ctx.source.executor
        val sender = ctx.source.sender

        val textComponent = Component.text()
            .append {
                Component.text("$agentName info")
                    .color(NamedTextColor.GREEN)
                    .decoration(TextDecoration.BOLD, true)
            }
            .append {
                Component.text("\nModel: ")
                    .append {
                        Component.text(model)
                            .color(NamedTextColor.YELLOW)
                    }
            }

        if ( executor is Player ) {
            val playerUuid = executor.uniqueId

            val job = pluginScope.launch {
                val playerContextSize = contextService.getContext(playerUuid).size
                val maxPlayerMessages = configurationManager.contextMaxPlayerMessages

                textComponent.append {
                    Component.text("Context used: ")
                        .append {
                            Component.text("\n$playerContextSize/$maxPlayerMessages/")
                                .color(NamedTextColor.YELLOW)
                        }
                }
            }

            job.invokeOnCompletion {
                sendInfo(textComponent, sender)
            }

            return Command.SINGLE_SUCCESS
        }

        sendInfo(textComponent, sender)
        return Command.SINGLE_SUCCESS
    }
}

private fun sendInfo(textComponent: TextComponent.Builder, sender: CommandSender) {
    sender.sendMessage(textComponent)
}