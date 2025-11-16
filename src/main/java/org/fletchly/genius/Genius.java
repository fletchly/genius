package org.fletchly.genius;

import org.bukkit.plugin.java.JavaPlugin;
import org.fletchly.genius.util.ConfigurationManager;

public final class Genius extends JavaPlugin {
    private static Genius instance;

    private static ConfigurationManager configurationManager;

    public static Genius getInstance() {
        if (instance == null) {
            throw new IllegalStateException("Can't get instance. Plugin not enabled yet!");
        }
        return instance;
    }

    @Override
    public void onEnable() {
        // Set instance
        instance = this;
    }

    @Override
    public void onDisable() {
        configurationManager = null;
        instance = null;
        getLogger().info("Goodbye!");
    }

    /**
     * Initializes the ConfigurationManager instance for the plugin. This method creates and assigns
     * a new {@link ConfigurationManager} using the current plugin's configuration and logger. It also
     * logs any validation errors encountered during the initialization of configuration properties.
     */
    private void initializeConfigurationManager() {
        getLogger().info("Initializing Configuration Manager...");
        configurationManager = new ConfigurationManager(getConfig(), getLogger());
        configurationManager.logValidationErrors();
    }
}
