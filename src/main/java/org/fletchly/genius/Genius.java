package org.fletchly.genius;

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.fletchly.genius.command.GeniusCommand;
import org.fletchly.genius.service.OllamaService;
import org.fletchly.genius.util.ConfigUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

public final class Genius extends JavaPlugin {
    private static Genius instance;

    @Getter
    private OllamaService ollamaService;

    @Getter
    private Logger pluginLogger;

    @Getter
    private FileConfiguration configuration;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        pluginLogger = getLogger();

        // Initialize and validate configuration
        boolean validConfig = initializeConfig();
        if (!validConfig) pluginLogger.warning("Invalid configuration. Skipping Ollama service initialization. Genius will be unavailable until configuration errors are resolved");
        ollamaService = validConfig ? initializeOllamaService() : null;
        registerCommands();

        pluginLogger.info("Successfully enabled Genius!");
    }

    @Override
    public void onDisable() {
        ollamaService.close();
        ollamaService = null;
        configuration = null;
        pluginLogger = null;
        instance = null;
    }

    public static Genius getInstance() {
        if (instance == null) {
            throw new IllegalStateException("Can't get instance. Plugin not enabled yet!");
        }
        return instance;
    }

    private boolean initializeConfig() {
        pluginLogger.info("Loading config.yml");
        configuration = getConfig();

        var validationErrors = ConfigUtil.validate(configuration, Map.of(
                "genius.agentName", ConfigUtil.ConfigType.STRING,
                "genius.systemPrompt", ConfigUtil.ConfigType.STRING,
                "ollama.baseUrl", ConfigUtil.ConfigType.STRING,
                "ollama.apiKey", ConfigUtil.ConfigType.STRING,
                "ollama.model", ConfigUtil.ConfigType.STRING,
                "ollama.temperature", ConfigUtil.ConfigType.DECIMAL,
                "ollama.topK", ConfigUtil.ConfigType.INTEGER,
                "ollama.topP", ConfigUtil.ConfigType.DECIMAL,
                "ollama.numPredict", ConfigUtil.ConfigType.INTEGER
        ));

        validationErrors.forEach(error -> {
            pluginLogger.warning(String.format("Error validating config: %s", error));
        });

        return validationErrors.isEmpty();
    }

    private OllamaService initializeOllamaService() {
        pluginLogger.info("Initializing Ollama service");

        return OllamaService.builder()
                .systemPrompt(Objects.requireNonNull(configuration.getString("genius.systemPrompt")).replaceAll("\n", ""))
                .baseUrl(configuration.getString("ollama.baseUrl"))
                .apiKey(configuration.getString("ollama.apiKey"))
                .model(configuration.getString("ollama.model"))
                .temperature(configuration.getDouble("ollama.temperature"))
                .topK(configuration.getInt("ollama.topK"))
                .topP(configuration.getDouble("ollama.topP"))
                .numPredict(configuration.getInt("ollama.numPredict"))
                .build();

    }

    private void registerCommands() {
        pluginLogger.info("Registering commands");
        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(
                    GeniusCommand.get().build(),
                    "Ask genius a question",
                    List.of("g", "ask")
            );
        });
    }
}
