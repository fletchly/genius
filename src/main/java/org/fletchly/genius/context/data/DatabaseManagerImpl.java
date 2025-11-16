package org.fletchly.genius.context.data;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.plugin.java.JavaPlugin;
import org.fletchly.genius.util.ConfigurationManager;
import org.intellij.lang.annotations.Language;

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
        @Language("SQLite")
        String initScript = """
                pragma foreign_keys = true;
                create table if not exists conversations
                (
                    id          INTEGER primary key autoincrement,
                    player_uuid TEXT    not null unique,
                    created_at  INTEGER not null,
                    updated_at  INTEGER not null
                );
                create table if not exists messages
                (
                    id              INTEGER primary key autoincrement,
                    conversation_id INTEGER not null
                        references conversations
                            on delete cascade,
                    role            TEXT    not null,
                    content         TEXT    not null,
                    created_at      INTEGER not null
                );
                create index if not exists idx_messages_conversation_id on messages (conversation_id);
                create index if not exists idx_messages_timestamp on messages (created_at);
                """;

        try (Connection c = ds.getConnection()) {
            for (String sql : initScript.split(";")) {
                String trimmed = sql.trim();
                if (trimmed.isEmpty()) {
                    continue; // ← Skip empty statements
                }
                try (Statement s = c.createStatement()) {
                    s.execute(trimmed);
                }
            }
        }
    }
}
