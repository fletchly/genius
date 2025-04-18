package org.fletchly.genius;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.fletchly.genius.Service.ApiService;
import org.fletchly.genius.Service.ChatHelper;
import org.jetbrains.annotations.NotNull;

public final class Genius extends JavaPlugin
{
    private ApiService api;

    @Override
    public void onEnable()
    {
        saveDefaultConfig();
        var config = getConfig();
        String apiKey = getConfig().getString("gemini-config.api-key");

        if (apiKey != null && !apiKey.isBlank())
        {
            api = new ApiService(config);
            getLogger().info("Loaded API Key Successfully and initialized API Service.");
        }
        else
        {
            getLogger().warning("API Key not found in config.yml. Please set it up.");
        }

        getLogger().info("Genius is enabled!");
    }

    @Override
    public void onDisable()
    {
        getLogger().info("Genius is disabled!");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, Command command, @NotNull String label, String @NotNull [] args)
    {
        if (command.getName().equalsIgnoreCase("genius")) {
            if (api == null)
            {
                sender.sendMessage("API Key is not set. Please check your config.yml.");
                return true;
            }

            if (args.length == 0) {
                sender.sendMessage("Usage: /genius <prompt>");
                return true;
            }

            String prompt = String.join(" ", args);
            try {
                String botName = getConfig().getString("bot-name");
                String response = api.getResponse(prompt);

                var messages = ChatHelper.buildBotMessage(botName, response);

                for (var message : messages)
                {
                    sender.sendMessage(message);
                }
            } catch (Exception e) {
                sender.sendMessage("An error occurred");
            }
            return true;
        }
        return false;
    }
}
