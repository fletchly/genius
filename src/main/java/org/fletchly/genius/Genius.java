package org.fletchly.genius;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.fletchly.genius.commands.GeniusCommand;
import org.fletchly.genius.service.api.ApiService;
import org.fletchly.genius.service.api.gemini.GeminiApiService;

@SuppressWarnings("UnstableApiUsage")
public final class Genius extends JavaPlugin
{
    private FileConfiguration config;
    private ApiService api;

    @Override
    public void onEnable()
    {
        saveDefaultConfig();

        config = getConfig();

        // TODO: Add more robust error handling for blank configs.
        String modelType = config.getString("api-config.model-type");
        String apiKey = config.getString("api-config.api-key");
        String baseUrl = config.getString("api-config.base-url");
        int maxTokens = Integer.parseInt(config.getString("api-config.max-tokens"));
        String systemContext = config.getString("api-config.system-context");


        // Check for model type in config
        if (modelType != null && !modelType.isBlank())
        {
            // Check for API Key in config
            if (apiKey != null && !apiKey.isBlank())
            {
                // Determine which API Service implementation to use based on config
                switch (modelType)
                {
                    case "gemini": // Use Gemini API service
                        api = new GeminiApiService(apiKey, baseUrl, maxTokens, systemContext);
                        getLogger().info("Using Gemini as Genius model");
                        break;
                    default: // Unknown model type
                        getLogger().warning("Unknown model type. Please check your config.yml");
                        break;
                }
                getLogger().info("Loaded API Service successfully");
            }
            else
            {
                // Notify if API key is not set
                getLogger().warning("API Key not found. Please specify it in config.yml");
            }
        }
        else
        {
            // Notify if Model type is not set
            getLogger().warning("Model type not found. Please specify it in config.yml");
        }

        // Register commands
        getLogger().info("Registering commands");
        registerCommands();

        getLogger().info("Successfully enabled Genius.");
    }

    @Override
    public void onDisable()
    {
        getLogger().info("Genius has shut down.");
    }

    /**
     * Register plugin commands
     */
    public void registerCommands()
    {
        // Add command listeners
        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            // Add main genius command
            var geniusCommand = GeniusCommand.getCommand(api, config.getString("bot-name"));

            // Add genius alias
            var geniusAlias = LiteralArgumentBuilder
                    .<CommandSourceStack>literal("g")
                    .redirect(geniusCommand)
                    .build();

            // Register commands
            commands.registrar().register(geniusCommand);
            commands.registrar().register(geniusAlias);
        });
    }
}
