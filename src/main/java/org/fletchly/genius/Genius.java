package org.fletchly.genius;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.fletchly.genius.commands.GeniusCommand;
import org.fletchly.genius.service.api.openai.OpenAiApiService;

import java.util.logging.Logger;

@SuppressWarnings("UnstableApiUsage")
public final class Genius extends JavaPlugin
{
    @Getter
    private static Genius instance;

    private FileConfiguration config;
    private Logger logger;
    private OpenAiApiService api;
    private boolean validConfig;

    @Override
    public void onEnable()
    {
        instance = this;
        saveDefaultConfig();

        // Initialize logger and config
        config = getConfig();
        logger = getLogger();

        // Mark config as valid initially
        validConfig = true;

        // Validate config
        String modelType = validateString("api-config.model-type");
        String apiKey = validateString("api-config.api-key");
        String baseUrl = validateString("api-config.base-url");
        int maxTokens = validateInt("api-config.max-tokens");
        String systemContext = validateString("api-config.system-context");

        if (validConfig)
        {
            // Initialize API Service
            api = new OpenAiApiService(apiKey, baseUrl, modelType, systemContext, maxTokens);
            logger.info("Successfully initialized API service for model " + modelType);
        } else
        {
            // Warn that API service was not initialized
            logger.warning("Skipping API service initialization due to improper configuration");
        }

        // Register commands
        logger.info("Registering commands");
        registerCommands();

        logger.info("Successfully enabled Genius " + getPluginMeta().getVersion());
    }

    @Override
    public void onDisable()
    {
        logger.info("Closing HTTP client");
        api.closeClient();

        logger.info("Successfully shut down Genius " + getPluginMeta().getVersion());
    }

    /**
     * Register plugin commands
     */
    public void registerCommands()
    {
        // Add command listeners
        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands ->
        {
            // Add main genius command
            var geniusCommand = GeniusCommand.getCommand(api, config.getString("bot-name"));

            // Add genius alias
            var geniusAlias = LiteralArgumentBuilder
                    .<CommandSourceStack>literal("g")
                    .requires(sender -> sender.getSender().hasPermission("genius.use"))
                    .redirect(geniusCommand)
                    .build();

            // Register commands
            commands.registrar().register(geniusCommand);
            commands.registrar().register(geniusAlias);
        });
    }

    /**
     * Validate that config string value exists and is not blank
     *
     * @param path Path in config.yml
     * @return Path value if found, empty string if not
     */
    private String validateString(String path)
    {
        String result = config.getString(path);

        if (result != null && !result.isBlank())
        {
            return result;
        }

        validConfig = false;
        logger.warning(String.format("Missing or blank config value for %s, please check your config.yml", path));
        return "";
    }

    /**
     * Validate that config int value exists and is not 0
     *
     * @param path Path in config.yml
     * @return Path value if found, empty string if not
     */
    private int validateInt(String path)
    {
        int result = config.getInt(path);

        if (result != 0)
        {
            return result;
        }

        validConfig = false;
        logger.warning(String.format("Missing or blank config value for %s, please check your config.yml", path));
        return 0;
    }
}
