package org.fletchly.genius.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import org.fletchly.genius.Genius;

import java.util.Objects;

import static io.papermc.paper.registry.keys.SoundEventKeys.ENTITY_EXPERIENCE_ORB_PICKUP;
import static net.kyori.adventure.sound.Sound.Emitter.self;
import static net.kyori.adventure.sound.Sound.Source.MASTER;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.GREEN;

public class GeniusCommand {
    public static LiteralArgumentBuilder<CommandSourceStack> get() {
        return Commands.literal("genius")
                .requires(sender -> sender.getSender().hasPermission("genius"))
                .then(Commands.argument("prompt", StringArgumentType.greedyString())
                        .executes(GeniusCommand::execute));
    }

    private static int execute(CommandContext<CommandSourceStack> ctx) {
        String prompt = ctx.getArgument("prompt", String.class);

        String response = Genius.getInstance().getOllamaService().generateChat(prompt);

        final Component chatResponse = text()
                .content("[")
                .append(text(Objects.requireNonNull(Genius.getInstance().getConfiguration().getString("genius.agentName")), GREEN))
                .append(text("] " + response))
                .build();


        ctx.getSource().getSender().sendMessage(chatResponse);
        ctx.getSource().getSender().playSound(Sound.sound(ENTITY_EXPERIENCE_ORB_PICKUP, MASTER, 1f, 1f), self());
        return Command.SINGLE_SUCCESS;
    }
}
