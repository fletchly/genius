package org.fletchly.genius.context.data;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.plugin.java.JavaPlugin;
import org.fletchly.genius.util.ConfigurationManager;

import javax.inject.Inject;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManagerImpl implements DatabaseManager {
    private final HikariDataSource ds;
    @Inject
    public DatabaseManagerImpl(JavaPlugin plugin, ConfigurationManager configurationManager) {
        plugin.getLogger().info("[DatabaseManager] Initializing context database");

        Path dbFile = plugin.getDataFolder().toPath().resolve(configurationManager.contextDbPath());
        String jdbcUrl = "jdbc:sqlite:" + dbFile;

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);
        config.setMaximumPoolSize(4);
        config.setConnectionTimeout(5000);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        this.ds = new HikariDataSource(config);

        try {
            setUpTables();
            plugin.getLogger().info("[DatabaseManager] SQLite database initialized at " + dbFile);
        } catch (SQLException e) {
            plugin.getLogger().warning("[DatabaseManager] Error initializing SQLite database");
        }
    }

    @Override
    public HikariDataSource getDataSource() {
        return ds;
    }

    @Override
    public void close() {
        if (ds != null) ds.close();
    }

    private void setUpTables() throws SQLException {
        String initScript = """
                PRAGMA foreign_keys = ON;
                CREATE TABLE IF NOT EXISTS conversations
                (
                    id          INTEGER PRIMARY KEY AUTO_INCREMENT,
                    player_uuid TEXT    NOT NULL,
                    created_at  INTEGER NOT NULL,
                    updated_at  INTEGER NOT NULL
                );
                CREATE TABLE IF NOT EXISTS messages
                (
                    id              INTEGER PRIMARY KEY AUTO_INCREMENT,
                    conversation_id INTEGER NOT NULL,
                    role            TEXT    NOT NULL,
                    content         TEXT    NOT NULL,
                    created_at      INTEGER NOT NULL,
                    FOREIGN KEY (conversation_id) REFERENCES conversations (id) ON DELETE CASCADE
                );
                CREATE INDEX idx_conversations_player_uuid ON conversations (player_uuid);
                CREATE INDEX idx_messages_conversation_id ON messages (conversation_id);
                CREATE INDEX idx_messages_timestamp ON messages (created_at);
                """;

        try (Connection c = ds.getConnection()) {
            for (String sql : initScript.split(";")) {
                try (Statement s = c.createStatement()) {
                    s.execute(sql.trim());
                }
            }
        }
    }
}
