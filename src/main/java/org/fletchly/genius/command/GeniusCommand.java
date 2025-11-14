package org.fletchly.genius.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;
import net.kyori.adventure.text.TextComponent;
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
import static net.kyori.adventure.text.format.NamedTextColor.GREEN;
import static net.kyori.adventure.text.format.NamedTextColor.RED;

public class GeniusCommand {
    private static final ComponentBuilder<TextComponent, TextComponent.Builder> ChatName = text()
            .content("[")
            .append(text(Objects.requireNonNullElse(
                    Genius
                            .getInstance()
                            .getConfiguration()
                            .getString("genius.agentName"),
                            "?"),
                    GREEN))
            .append(text("] "));

    public static LiteralArgumentBuilder<CommandSourceStack> get() {
        return Commands.literal("genius")
                .requires(sender -> sender.getSender().hasPermission("genius"))
                .then(Commands.argument("prompt", StringArgumentType.greedyString())
                        .executes(GeniusCommand::execute));
    }

    private static int execute(CommandContext<CommandSourceStack> ctx) {
        String prompt = ctx.getArgument("prompt", String.class);
        final Genius instance = Genius.getInstance();
        final BukkitScheduler scheduler = instance.getServer().getScheduler();

        if (instance.getOllamaService() == null) {
            final Component chatResponse = ChatName
                    .append(text("Genius is not set up correctly", RED))
                    .build();
            ctx.getSource().getSender().sendMessage(chatResponse);
            ctx.getSource().getSender().playSound(Sound.sound(BLOCK_GLASS_BREAK, MASTER, 1f, 1f), self());
            Genius.getInstance().getLogger().warning("Genius service is not initialized. Check your configuration");
        }

        scheduler.runTaskAsynchronously(instance, () -> instance.getOllamaService().generateChat(prompt)
                .exceptionally(ex -> {
                    Throwable cause = ex.getCause();
                    String userMessage;
                    Throwable exception;

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

                    scheduler.runTask(instance, () -> {
                        final Component chatResponse = ChatName
                                .append(text(userMessage, RED))
                                .build();
                        ctx.getSource().getSender().sendMessage(chatResponse);
                        ctx.getSource().getSender().playSound(Sound.sound(BLOCK_GLASS_BREAK, MASTER, 1f, 1f), self());
                        Genius.getInstance().getLogger().warning("Genius ran into a problem: " + Arrays.toString(exception.getStackTrace()));
                    });

                    return "Error";
                })
                .thenAccept(response -> scheduler.runTask(instance, () -> {
                    final Component chatResponse = ChatName
                            .append(text(response))
                            .build();
                    ctx.getSource().getSender().sendMessage(chatResponse);
                    ctx.getSource().getSender().playSound(Sound.sound(ENTITY_EXPERIENCE_ORB_PICKUP, MASTER, 1f, 1f), self());
                })));
        return Command.SINGLE_SUCCESS;
    }
}
