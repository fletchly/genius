package org.fletchly.genius.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.fletchly.genius.chat.ChatManager;
import org.fletchly.genius.util.ConfigurationManager;

import javax.inject.Inject;

import java.util.Objects;
import java.util.UUID;
import java.util.logging.Logger;

import static io.papermc.paper.registry.keys.SoundEventKeys.BLOCK_GLASS_BREAK;
import static io.papermc.paper.registry.keys.SoundEventKeys.ENTITY_EXPERIENCE_ORB_PICKUP;
import static net.kyori.adventure.sound.Sound.Emitter.self;
import static net.kyori.adventure.sound.Sound.Source.MASTER;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.GREEN;
import static net.kyori.adventure.text.format.NamedTextColor.RED;

public class AskCommand {
    private final Component displayName;
    private final ChatManager chatManager;
    private final JavaPlugin plugin;

    @Inject
    public AskCommand(ConfigurationManager configurationManager, ChatManager chatManager, JavaPlugin plugin) {
        this.chatManager = chatManager;
        this.displayName = text("[").append(text(configurationManager.geniusAgentName(), GREEN)).append(text("] "));
        this.plugin = plugin;
    }

    public LiteralCommandNode<CommandSourceStack> createCommandNode() {
        return Commands.literal("ask")
                .requires(sender ->
                        sender.getExecutor() instanceof Player &&
                        sender.getSender().hasPermission("genius.ask")
                )
                .then(
                        Commands.argument("prompt", StringArgumentType.greedyString()
                )
                .executes(this::execute))
                .build();
    }

    private int execute(CommandContext<CommandSourceStack> ctx) {
        String prompt = ctx.getArgument("prompt", String.class);
        UUID playerUuid = Objects.requireNonNull(ctx.getSource().getExecutor()).getUniqueId();

        chatManager.generateChat(playerUuid, prompt)
                .exceptionally(throwable -> sendFailure(throwable, ctx.getSource().getSender()))
                .thenAccept(response -> sendSuccess(response, ctx.getSource().getSender()));

        return Command.SINGLE_SUCCESS;
    }

    private void sendSuccess(String message, CommandSender sender) {
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            sender.sendMessage(displayName.append(text(message)));
            sender.playSound(Sound.sound(ENTITY_EXPERIENCE_ORB_PICKUP, MASTER, 1f, 1f), self());
        });
    }

    private String sendFailure(Throwable cause, CommandSender sender) {
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            sender.sendMessage(displayName.append(text(cause.getMessage(), RED)));
            sender.playSound(Sound.sound(BLOCK_GLASS_BREAK, MASTER, 1f, 1f), self());
        });
        return null;
    }
}
