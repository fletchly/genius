package org.fletchly.genius.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import org.fletchly.genius.Genius;
import org.fletchly.genius.service.api.openai.OpenAiApiService;
import org.fletchly.genius.util.ChatHelper;

import java.util.Arrays;

import static org.bukkit.plugin.java.JavaPlugin.getPlugin;

/**
 * Genius command class
 */
@SuppressWarnings("UnstableApiUsage")
public class GeniusCommand
{
    private static final Genius genius = Genius.getInstance();

    /**
     * Build Genius command
     *
     * @param api     the api service
     * @param botName the name of the chatbot to appear in chat
     * @return Command literal
     */
    public static LiteralCommandNode<CommandSourceStack> getCommand(OpenAiApiService api, String botName)
    {
        // /genius <prompt>
        return Commands.literal("genius")
                .requires(sender -> sender.getSender().hasPermission("genius.use"))
                .then(Commands.argument("prompt", StringArgumentType.greedyString())
                        .executes(ctx -> executeGeniusCommand(ctx, api, botName))
                )
                .build();
    }

    /**
     * Execute the logic for the genius command
     *
     * @param ctx     command context
     * @param api     api service
     * @param botName name of chatbot to appear in chat
     * @return integer representing command success
     */
    private static int executeGeniusCommand(final CommandContext<CommandSourceStack> ctx, OpenAiApiService api, String botName)
    {
        // Get prompt from arguments
        String prompt = ctx.getArgument("prompt", String.class);

        // Notify if the API has not been initialized correctly
        if (api == null)
        {
            ctx.getSource().getSender().sendRichMessage("<red>API Key is not set! Please check your config.yml");
            return Command.SINGLE_SUCCESS;
        }

        // Sanitize user prompt
        String sanitizedPrompt = prompt.replaceAll("[^\\w\\s.,!?@#\\-]", "").trim();

        try
        {
            // Get response from the API
            var response = api.getResponse(sanitizedPrompt);

            // Format response to support multiple lines
            var responseFormatted = ChatHelper.buildMessage(botName, response);

            // Play a sound for the sender to notify them of a response
            ctx.getSource().getSender().playSound(Sound.sound(org.bukkit.Sound.ENTITY_EXPERIENCE_ORB_PICKUP, Sound.Source.MASTER, 1f, 1f), Sound.Emitter.self());

            // Send all lines of the response
            for (var message : responseFormatted)
            {
                ctx.getSource().getSender().sendMessage(message);
            }
        } catch (Exception e)
        {
            genius.getComponentLogger().error(Component.text("Error making request"), e);
            ctx.getSource().getSender().sendRichMessage("<red>An error occurred: " + e.getMessage());
        }

        return Command.SINGLE_SUCCESS;
    }
}
