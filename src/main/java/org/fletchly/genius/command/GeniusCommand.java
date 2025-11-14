package org.fletchly.genius.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import lombok.Getter;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitScheduler;
import org.fletchly.genius.Genius;
import org.fletchly.genius.client.ollama.OllamaClient;

import java.util.Arrays;
import java.util.Objects;

import static io.papermc.paper.registry.keys.SoundEventKeys.BLOCK_GLASS_BREAK;
import static io.papermc.paper.registry.keys.SoundEventKeys.ENTITY_EXPERIENCE_ORB_PICKUP;
import static net.kyori.adventure.sound.Sound.Emitter.self;
import static net.kyori.adventure.sound.Sound.Source.MASTER;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.*;

/**
 * /genius command structure and logic
 */
public class GeniusCommand {
    // Name to appear in chat when sending responses
    private static final ComponentBuilder<TextComponent, TextComponent.Builder> chatName = text()
            .content("[")
            .append(text(Objects.requireNonNullElse(
                    Genius
                            .getInstance()
                            .getConfiguration()
                            .getString("genius.agentName"),
                            "?"),
                    GREEN))
            .append(text("] "));

    // Command structure
    @Getter
    private static final LiteralCommandNode<CommandSourceStack> command = Commands.literal("genius")
            .requires(sender ->
                    sender.getSender().hasPermission("genius"))
            .then(Commands.argument("prompt", StringArgumentType.greedyString())
                    .executes(GeniusCommand::execute)).build();

    /**
     * Execution logic for /genius command
     * @param ctx Command context
     * @return Commands.SINGLE_SUCCESS (1) always
     */
    private static int execute(CommandContext<CommandSourceStack> ctx) {
        // Get the current plugin instance, task scheduler, and command sender.
        final Genius instance = Genius.getInstance();
        final BukkitScheduler scheduler = instance.getServer().getScheduler();
        final CommandSender sender = ctx.getSource().getSender();

        // Parse prompt
        String prompt = ctx.getArgument("prompt", String.class);

        // Display prompt for sender
        final Component chatPrompt =
                text(String.format("[%s] %s", sender.getName(), prompt), GRAY);
        sender.sendMessage(chatPrompt);

        // Check that ollama service was initialized correctly
        if (instance.getOllamaService() == null) {
            final Component chatResponse = chatName
                    .append(text("Genius is not set up correctly", RED))
                    .build();
            sender.sendMessage(chatResponse);
            sender.playSound(Sound.sound(BLOCK_GLASS_BREAK, MASTER, 1f, 1f), self());
            Genius.getInstance().getLogger().warning("Genius service is not initialized. Check your configuration");
        }

        // Asynchronously make a request to Ollama.
        scheduler.runTaskAsynchronously(instance, () -> instance.getOllamaService().generateChat(prompt)
                .exceptionally(ex -> {
                    // Unwrap exception
                    Throwable cause = ex.getCause();
                    String userMessage;
                    Throwable exception;

                    // Set qualified messages
                    switch (cause) {
                        case OllamaClient.OllamaHttpException httpEx -> {
                            userMessage = "Something went wrong when communicating with server. Please try again";
                            exception = httpEx;
                        }
                        case OllamaClient.OllamaParseException parseEx -> {
                            userMessage = "Something went wrong when understanding request/response. Please try again";
                            exception = parseEx;
                        }
                        case OllamaClient.OllamaNetworkException netEx -> {
                            userMessage = "Network error";
                            exception = netEx;
                        }
                        case null, default -> {
                            userMessage = "Unexpected error";
                            exception = cause;
                        }
                    }

                    // Log error and inform user of failure
                    scheduler.runTask(instance, () -> {
                        final Component chatResponse = chatName
                                .append(text(userMessage, RED))
                                .build();
                        sender.sendMessage(chatResponse);
                        sender.playSound(Sound.sound(BLOCK_GLASS_BREAK, MASTER, 1f, 1f), self());
                        Genius.getInstance().getLogger().warning("Genius ran into a problem: " + Arrays.toString(exception.getStackTrace()));
                    });

                    return "I'M DEAD X_x"; // never used
                })
                // Display response in chat
                .thenAccept(response -> scheduler.runTask(instance, () -> {
                    final Component chatResponse = chatName
                            .append(text(response))
                            .build();
                    sender.sendMessage(chatResponse);
                    sender.playSound(Sound.sound(ENTITY_EXPERIENCE_ORB_PICKUP, MASTER, 1f, 1f), self());
                }))
        );
        return Command.SINGLE_SUCCESS;
    }
}
