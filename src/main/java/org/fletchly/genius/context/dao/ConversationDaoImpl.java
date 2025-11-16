package org.fletchly.genius.context.dao;

import org.fletchly.genius.context.data.DatabaseManager;
import org.fletchly.genius.context.model.ContextConversation;
import org.intellij.lang.annotations.Language;

import javax.inject.Inject;
import java.sql.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.logging.Logger;

public class ConversationDaoImpl implements ConversationDao {
    private final ExecutorService executorService;
    private final DatabaseManager db;
    private final Logger logger;

    @Inject
    public ConversationDaoImpl(ExecutorService executorService, DatabaseManager db, Logger logger) {
        this.executorService = executorService;
        this.db = db;
        this.logger = logger;
    }

    @Override
    public CompletableFuture<Optional<ContextConversation>> getOrInsertByPlayerUuid(UUID playerUuid) {
        @Language("SQLite")
        String sqlQuery = "SELECT id, created_at, updated_at FROM conversations WHERE player_uuid = ?;";
        return CompletableFuture.supplyAsync(() -> {
            try (Connection c = db.getDataSource().getConnection()) {
                try (PreparedStatement ps = c.prepareStatement(sqlQuery)) {
                    ps.setString(1, playerUuid.toString());
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            return Optional.of(ContextConversation.builder()
                                    .id(rs.getLong("id"))
                                    .playerUuid(playerUuid)
                                    .createdAt(rs.getLong("created_at"))
                                    .updatedAt(rs.getLong("updated_at"))
                                    .messages(List.of())
                                    .build());
                        }
                    }
                }

                @Language("SQLite")
                String sqlInsert = """
                INSERT INTO conversations (player_uuid, created_at, updated_at) VALUES (?, ?, ?)
                ON CONFLICT (player_uuid) DO UPDATE set updated_at = excluded.updated_at;
                """;

                long now = System.currentTimeMillis();

                try (PreparedStatement ps = c.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, playerUuid.toString());
                    ps.setLong(2, now);
                    ps.setLong(3, now);
                    ps.executeUpdate();
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        return Optional.of(ContextConversation.builder()
                                .id(keys.getInt(1))
                                .playerUuid(playerUuid)
                                .createdAt(now)
                                .updatedAt(now)
                                .messages(List.of())
                                .build());
                    }
                }
            } catch (SQLException ex) {
                logger.warning("Database error (insert or get message): " + ex);
                return Optional.empty();
            }
        }, executorService);
    }

    @Override
    public CompletableFuture<Void> deleteByPlayerUuid(UUID playerUuid) {
        @Language("SQLite")
        String sql = "DELETE from conversations WHERE player_uuid = ?";

        return CompletableFuture.runAsync(() -> {
            try (Connection c = db.getDataSource().getConnection();
                 PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, playerUuid.toString());
                ps.executeUpdate();
            } catch (SQLException ex) {
                logger.warning("Database error (delete message): " + ex);
            }
        }, executorService);
    }
}
