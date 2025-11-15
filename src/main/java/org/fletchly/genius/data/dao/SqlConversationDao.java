package org.fletchly.genius.data.dao;

import org.fletchly.genius.data.DatabaseManager;
import org.fletchly.genius.data.model.Conversation;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public class SqlConversationDao implements ConversationDao {
    private final DatabaseManager db;
    private final ExecutorService executor;

    public SqlConversationDao(DatabaseManager db, ExecutorService executor) {
        this.db = db;
        this.executor = executor;
    }

    @Override
    public CompletableFuture<Conversation> getOrCreateForPlayer(UUID playerUuid) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                try (PreparedStatement ps = db.getConnection().prepareStatement(
                        "SELECT id, created_at, updated_at FROM context.conversations WHERE player_uuid = ?;"
                )) {
                    ps.setString(1, playerUuid.toString());
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        int id = rs.getInt("id");
                        long created = rs.getLong("created_at");
                        long updated = rs.getLong("updated_at");
                        return Conversation.builder()
                                .id(id)
                                .created(created)
                                .updated(updated)
                                .build();
                    }
                }

                // Create new
                long now = System.currentTimeMillis();
                try (PreparedStatement ps = db.getConnection().prepareStatement(
                        "INSERT INTO context.conversations(player_uuid, created_at, updated_at) VALUES (?, ?, ?);",
                        Statement.RETURN_GENERATED_KEYS
                )) {
                    ps.setString(1, playerUuid.toString());
                    ps.setLong(2, now);
                    ps.setLong(3, now);
                    ps.executeUpdate();
                    ResultSet keys = ps.getGeneratedKeys();
                    keys.next();
                    int id = keys.getInt(1);
                    return Conversation.builder()
                            .id(id)
                            .playerUuid(playerUuid)
                            .created(now)
                            .updated(now)
                            .messages(List.of())
                            .build();
                }
            } catch (SQLException e) {
                throw new DaoException("Error getting/setting conversation data for player uuid=" + playerUuid, e);
            }
        }, executor);
    }

    @Override
    public CompletableFuture<Void> updateTimestamp(int conversationId) {
        return CompletableFuture.runAsync(() -> {
            try (PreparedStatement ps = db.getConnection().prepareStatement(
                    "UPDATE context.conversations SET updated_at = ? WHERE id = ?;"
            )) {
                ps.setLong(1, System.currentTimeMillis());
                ps.setInt(2, conversationId);
                ps.executeUpdate();
            } catch (SQLException e) {
                throw new DaoException("Error updating timestamp for conversation id=" + conversationId, e);
            }
        }, executor);
    }

    @Override
    public CompletableFuture<Void> deleteConversation(int conversationId) {
        return CompletableFuture.runAsync(() -> {
            try (PreparedStatement ps = db.getConnection().prepareStatement(
                    "DELETE FROM context.conversations WHERE id = ?;"
            )) {
                ps.setInt(1, conversationId);
                ps.executeUpdate();
            } catch (SQLException e) {
                throw new DaoException("Error deleting conversation id=" + conversationId, e);
            }
        }, executor);
    }
}
