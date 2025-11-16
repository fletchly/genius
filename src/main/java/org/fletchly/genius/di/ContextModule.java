package org.fletchly.genius.di;

import dagger.Module;
import dagger.Provides;
import org.bukkit.plugin.java.JavaPlugin;
import org.fletchly.genius.context.data.DatabaseManager;
import org.fletchly.genius.context.data.DatabaseManagerImpl;
import org.fletchly.genius.util.ConfigurationManager;

import javax.inject.Singleton;

@Module
public class ContextModule {
    @Provides
    @Singleton
    DatabaseManager provideDatabaseManager(JavaPlugin javaPlugin, ConfigurationManager configurationManager) {
        return new DatabaseManagerImpl(javaPlugin, configurationManager);
    }
}
