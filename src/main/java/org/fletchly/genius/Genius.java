package org.fletchly.genius;

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


        if (modelType != null && !modelType.isBlank())
        {
            if (apiKey != null && !apiKey.isBlank())
            {
                switch (modelType)
                {
                    case "gemini":
                        api = new GeminiApiService(apiKey, baseUrl, maxTokens, systemContext);
                        getLogger().info("Loaded API Key Successfully and initialized Gemini API Service.");
                        break;
                    default:
                        getLogger().warning("Unknown model type. Please check your config.yml");
                }
            }
            else
            {
                getLogger().warning("API Key not found. Please specify it in config.yml");
            }
        }
        else
        {
            getLogger().warning("Model type not found. Please specify it in config.yml");
        }

        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(GeniusCommand.constructGeniusCommand(api, config.getString("bot-name")));
        });

        getLogger().info("Successfully enabled Genius.");
    }

    @Override
    public void onDisable()
    {
        getLogger().info("Genius has shut down.");
    }
}
