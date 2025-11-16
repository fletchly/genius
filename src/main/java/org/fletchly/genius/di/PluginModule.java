package org.fletchly.genius.di;

import dagger.Module;
import dagger.Provides;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.fletchly.genius.util.ConfigurationManager;
import org.fletchly.genius.util.ConfigurationManagerImpl;

import javax.inject.Singleton;
import java.util.logging.Logger;

@Module
public class PluginModule {
    private final JavaPlugin plugin;

    public PluginModule(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Provides
    @Singleton
    JavaPlugin providePlugin() {
        return plugin;
    }

    @Provides
    @Singleton
    Logger provideLogger(JavaPlugin javaPlugin) {
        return javaPlugin.getLogger();
    }

    @Provides
    @Singleton
    FileConfiguration provideConfiguration(JavaPlugin javaPlugin) {
        return javaPlugin.getConfig();
    }

    @Provides
    @Singleton
    ConfigurationManager provideConfigurationManager(FileConfiguration fileConfiguration, Logger logger) {
        return new ConfigurationManagerImpl(fileConfiguration, logger);
    }
}
