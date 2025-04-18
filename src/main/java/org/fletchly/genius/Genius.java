package org.fletchly.genius;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.fletchly.genius.Service.ApiService;
import org.fletchly.genius.Service.ChatHelper;
import org.jetbrains.annotations.NotNull;

public final class Genius extends JavaPlugin
{
    // ApiService instance
    private ApiService api;

    @Override
    public void onEnable()
    {
        // Load the configuration file
        saveDefaultConfig();
        var config = getConfig();
        String apiKey = getConfig().getString("gemini-config.api-key");

        // Initialize the ApiService with the API key
        if (apiKey != null && !apiKey.isBlank())
        {
            api = new ApiService(config);
            getLogger().info("Loaded API Key Successfully and initialized API Service.");
        }
        else
        {
            // Log a warning if the API key is not found
            getLogger().warning("API Key not found in config.yml. Please set it up.");
        }

        getLogger().info("Successfully enabled Genius.");
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
            // Check if the API key is set
            if (api == null)
            {
                sender.sendMessage("API Key is not set. Please check your config.yml.");
                return true;
            }

            // Respond with usage if no arguments are provided
            if (args.length == 0) {
                sender.sendMessage("Usage: /genius <prompt>");
                return true;
            }

            // Join the arguments to form the prompt
            String prompt = String.join(" ", args);
            try {
                // Get the bot name from the configuration
                String botName = getConfig().getString("bot-name");

                // Get the response from the API
                String response = api.getResponse(prompt);

                // Build the response message
                var messages = ChatHelper.buildBotMessage(botName, response);

                // Send the response message to the command sender
                for (var message : messages)
                {
                    sender.sendMessage(message);
                }
            } catch (Exception e) {
                // Handle any exceptions that occur during the API call
                sender.sendMessage("An error occurred");
            }
            return true;
        }
        return false;
    }
}
