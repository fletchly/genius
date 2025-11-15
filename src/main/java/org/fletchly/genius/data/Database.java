package org.fletchly.genius.data;

import lombok.Getter;
import org.fletchly.genius.Genius;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Database manager for interacting with SQLite DB
 */
public class Database {
    @Getter
    private final Connection connection;

    private final Genius genius;

    public Database(File file, Genius genius) throws SQLException, IOException {
        // Get current plugin instance
        this.genius = genius;

        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException ignored) {
            // The Sqlite driver is bundled with Paper so it should always exist
        }

        String dbUrl = "jdbc:sqlite:" + file.getAbsolutePath();
        this.connection = DriverManager.getConnection(dbUrl);

        // Foreign keys disabled by default in SQLite
        try (Statement st = connection.createStatement()) {
            st.execute("PRAGMA foreign_keys = ON;");
        }

        initSchema();
    }

    public void close() throws SQLException {
        connection.close();
    }

    private void initSchema() throws SQLException, IOException {
        File ddlFile = new File(genius.getDataFolder(), "db/context.ddl");

        if (!ddlFile.exists()) {
            throw new FileNotFoundException("context.ddl not found: " + ddlFile.getAbsolutePath());
        }

        String ddl = Files.readString(ddlFile.toPath());

        try (Statement st = connection.createStatement()) {
            for (String sql : ddl.split(";")) {
                String trimmed = sql.trim();
                if (!trimmed.isEmpty()) {
                    st.execute(trimmed);
                }
            }
        }
    }
}
