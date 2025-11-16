package org.fletchly.genius.util;

import org.jetbrains.annotations.NotNull;

public interface ConfigurationManager {

    @NotNull String geniusAgentName();

    @NotNull String geniusSystemPrompt();

    @NotNull String ollamaBaseUrl();

    @NotNull String ollamaApiKey();

    @NotNull String ollamaModel();

    @NotNull Double ollamaTemperature();

    @NotNull Integer ollamaTopK();

    @NotNull Double ollamaTopP();

    @NotNull Integer ollamaNumPredict();

    @NotNull String contextDbPath();

    @NotNull Integer contextMaxPlayerMessages();

    /**
     * Validates essential configuration properties and logs warnings for any properties
     * that are null or invalid. For each invalid or missing property, a default value
     * is retrieved and a warning message is logged to indicate the issue and fallback behavior.
     */
    void logValidationErrors();
}
