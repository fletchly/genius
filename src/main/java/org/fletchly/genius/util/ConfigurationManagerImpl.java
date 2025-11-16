package org.fletchly.genius.util;

import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * The ConfigurationManager class provides access to configuration values for the application,
 * managing default values and logging warnings for missing or invalid settings. Configuration
 * properties are initialized from a {@link FileConfiguration} object and fall back to default
 * settings if the provided configuration is invalid or incomplete.
 */
public class ConfigurationManagerImpl implements ConfigurationManager {
    private final Configuration defaults;
    private final Logger logger;
    private final String geniusAgentName;
    private final String geniusSystemPrompt;
    private final String ollamaBaseUrl;
    private final String ollamaApiKey;
    private final String ollamaModel;
    private final Double ollamaTemperature;
    private final Integer ollamaTopK;
    private final Double ollamaTopP;
    private final Integer ollamaNumPredict;

    /**
     * Constructs a ConfigurationManager instance and initializes configuration values from
     * the provided file configuration.
     *
     * @param fileConfiguration the file configuration object containing the configuration values
     * @param logger            the logger instance to log warnings and messages
     */
    @Inject
    public ConfigurationManagerImpl(FileConfiguration fileConfiguration, Logger logger) {
        defaults = fileConfiguration.getDefaults();
        this.logger = logger;

        geniusAgentName = fileConfiguration.getString(Paths.GENIUS_AGENT_NAME);
        geniusSystemPrompt = fileConfiguration.getString(Paths.GENIUS_SYSTEM_PROMPT);
        ollamaBaseUrl = fileConfiguration.getString(Paths.OLLAMA_BASE_URL);
        ollamaApiKey = fileConfiguration.getString(Paths.OLLAMA_API_KEY);
        ollamaModel = fileConfiguration.getString(Paths.OLLAMA_MODEL);
        ollamaTemperature = fileConfiguration.getDouble(Paths.OLLAMA_TEMPERATURE);
        ollamaTopK = fileConfiguration.getInt(Paths.OLLAMA_TOP_K);
        ollamaTopP = fileConfiguration.getDouble(Paths.OLLAMA_TOP_P);
        ollamaNumPredict = fileConfiguration.getInt(Paths.OLLAMA_NUM_PREDICT);
    }

    @Override
    public @NotNull String geniusAgentName() {
        if (geniusAgentName == null) {
            logInvalidProperty(Paths.GENIUS_AGENT_NAME);
            return Objects.requireNonNull(defaults.getString(Paths.GENIUS_AGENT_NAME));
        }
        return geniusAgentName;
    }

    @Override
    public @NotNull String geniusSystemPrompt() {
        if (geniusSystemPrompt == null) {
            logInvalidProperty(Paths.GENIUS_SYSTEM_PROMPT);
            return Objects.requireNonNull(defaults.getString(Paths.GENIUS_SYSTEM_PROMPT));
        }
        return geniusSystemPrompt;
    }

    @Override
    public @NotNull String ollamaBaseUrl() {
        if (ollamaBaseUrl == null) {
            logInvalidProperty(Paths.OLLAMA_BASE_URL);
            return Objects.requireNonNull(defaults.getString(Paths.OLLAMA_BASE_URL));
        }
        return ollamaBaseUrl;
    }

    @Override
    public @NotNull String ollamaApiKey() {
        if (ollamaApiKey == null) {
            logInvalidProperty(Paths.OLLAMA_API_KEY);
            return Objects.requireNonNull(defaults.getString(Paths.OLLAMA_API_KEY));
        }
        return ollamaApiKey;
    }

    @Override
    public @NotNull String ollamaModel() {
        if (ollamaModel == null) {
            logInvalidProperty(Paths.OLLAMA_MODEL);
            return Objects.requireNonNull(defaults.getString(Paths.OLLAMA_MODEL));
        }
        return ollamaModel;
    }

    @Override
    public @NotNull Double ollamaTemperature() {
        if (ollamaTemperature == null) {
            logInvalidProperty(Paths.OLLAMA_TEMPERATURE);
            return defaults.getDouble(Paths.OLLAMA_TEMPERATURE);
        }
        return ollamaTemperature;
    }

    @Override
    public @NotNull Integer ollamaTopK() {
        if (ollamaTopK == null) {
            logInvalidProperty(Paths.OLLAMA_TOP_K);
            return defaults.getInt(Paths.OLLAMA_TOP_K);
        }
        return ollamaTopK;
    }

    @Override
    public @NotNull Double ollamaTopP() {
        if (ollamaTopP == null) {
            logInvalidProperty(Paths.OLLAMA_TOP_P);
            return defaults.getDouble(Paths.OLLAMA_TOP_P);
        }
        return ollamaTopP;
    }

    @Override
    public @NotNull Integer ollamaNumPredict() {
        if (ollamaNumPredict == null) {
            logInvalidProperty(Paths.OLLAMA_NUM_PREDICT);
            return defaults.getInt(Paths.OLLAMA_NUM_PREDICT);
        }
        return ollamaNumPredict;
    }

    @Override
    public void logValidationErrors() {
        geniusAgentName();
        geniusSystemPrompt();
        ollamaBaseUrl();
        ollamaApiKey();
        ollamaModel();
        ollamaTemperature();
        ollamaTopK();
        ollamaTopP();
        ollamaNumPredict();
    }

    /**
     * Logs a warning message indicating that a configuration property at the specified path is
     * missing or invalid and provides the default value being used as a fallback.
     *
     * @param path the configuration path of the property that is invalid or missing
     */
    private void logInvalidProperty(String path) {
        Object defaultValue = defaults.get(path);
        logger.warning(
                String.format("[ConfigurationManager] Config value at path %s is missing or invalid! Using default value of %s", path, defaultValue)
        );
    }

    /**
     * The Paths class provides constants representing configuration paths used within the
     * application. These paths are used to retrieve various configuration values from the
     * configuration files.
     */
    public static final class Paths {
        public static final String GENIUS_AGENT_NAME = "genius.agentName";
        public static final String GENIUS_SYSTEM_PROMPT = "genius.systemPrompt";
        public static final String OLLAMA_BASE_URL = "ollama.baseUrl";
        public static final String OLLAMA_API_KEY = "ollama.apiKey";
        public static final String OLLAMA_MODEL = "ollama.model";
        public static final String OLLAMA_TEMPERATURE = "ollama.temperature";
        public static final String OLLAMA_TOP_K = "ollama.topK";
        public static final String OLLAMA_TOP_P = "ollama.topP";
        public static final String OLLAMA_NUM_PREDICT = "ollama.numPredict";
    }
}
