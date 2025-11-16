package org.fletchly.genius.di;

import dagger.Module;
import dagger.Provides;
import org.bukkit.plugin.java.JavaPlugin;
import org.fletchly.genius.context.ContextService;
import org.fletchly.genius.context.ContextServiceImpl;
import org.fletchly.genius.context.dao.ConversationDao;
import org.fletchly.genius.context.dao.ConversationDaoImpl;
import org.fletchly.genius.context.dao.MessageDao;
import org.fletchly.genius.context.dao.MessageDaoImpl;
import org.fletchly.genius.context.data.DatabaseManager;
import org.fletchly.genius.context.data.DatabaseManagerImpl;
import org.fletchly.genius.util.ConfigurationManager;

import javax.inject.Singleton;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

@Module
public class ContextModule {
    @Provides
    @Singleton
    ExecutorService provideExecutor() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }

    @Provides
    @Singleton
    DatabaseManager provideDatabaseManager(JavaPlugin javaPlugin, ConfigurationManager configurationManager) {
        return new DatabaseManagerImpl(javaPlugin, configurationManager);
    }

    @Provides
    @Singleton
    MessageDao provideMessageDao(ExecutorService executorService, DatabaseManager db, Logger logger) {
        return new MessageDaoImpl(executorService, db, logger);
    }

    @Provides
    @Singleton
    ConversationDao provideConversationDao(ExecutorService executorService, DatabaseManager db, Logger logger) {
        return new ConversationDaoImpl(executorService, db, logger);
    }

    @Provides
    @Singleton
    ContextService provideContextService(MessageDao messageDao, ConversationDao conversationDao, ConfigurationManager configurationManager) {
        return new ContextServiceImpl(messageDao, conversationDao, configurationManager);
    }
}
