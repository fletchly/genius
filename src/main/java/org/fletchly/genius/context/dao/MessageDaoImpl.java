package org.fletchly.genius.context.dao;

import org.fletchly.genius.context.data.DatabaseManager;
import org.fletchly.genius.context.model.ContextMessage;
import org.intellij.lang.annotations.Language;

import javax.inject.Inject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.logging.Logger;

public class MessageDaoImpl implements MessageDao {
    private final ExecutorService executorService;
    private final DatabaseManager db;
    private final Logger logger;

    @Inject
    public MessageDaoImpl(ExecutorService executorService, DatabaseManager db, Logger logger) {
        this.executorService = executorService;
        this.db = db;
        this.logger = logger;
    }

    @Override
    public CompletableFuture<Optional<List<ContextMessage>>> findByConversationId(long conversationId) {
        @Language("SQLite")
        String sql = "SELECT id, role, content, created_at FROM messages WHERE conversation_id = ?;";

        return CompletableFuture.supplyAsync(() -> {
            try (Connection c = db.getDataSource().getConnection();
                PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setLong(1, conversationId);
                try (ResultSet rs = ps.executeQuery()) {
                    List<ContextMessage> messages = new ArrayList<>();
                    while (rs.next()) {
                        messages.add(
                                ContextMessage.builder()
                                        .conversationId(conversationId)
                                        .role(rs.getString("rs"))
                                        .content(rs.getString("content"))
                                        .createdAt(rs.getLong("created_at"))
                                        .build()
                        );
                    }
                    return Optional.of(messages);
                }
            } catch (SQLException ex) {
                logger.warning("Database error, (returning empty): " + ex);
                return Optional.empty();
            }
        }, executorService);
    }

    @Override
    public CompletableFuture<Void> insert(ContextMessage message) {
        @Language("SQLite")
        String sql = "INSERT INTO messages(conversation_id, role, content, created_at) VALUES(?, ?, ?, ?);";

        long now = System.currentTimeMillis();

        return CompletableFuture.runAsync(() -> {
            try (Connection c = db.getDataSource().getConnection();
                 PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setLong(1, message.conversationId());
                ps.setString(2, message.role());
                ps.setString(3, message.content());
                ps.setLong(4, now);
                ps.executeUpdate();
            } catch (SQLException ex) {
                logger.warning("Database error (insert new message): " + ex);
            }
        }, executorService);
    }

    @Override
    public CompletableFuture<Void> deleteById(long id) {
        @Language("SQLite")
        String sql = "DELETE FROM messages WHERE id = ?";

        return CompletableFuture.runAsync(() -> {
            try (Connection c = db.getDataSource().getConnection();
                 PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setLong(1, id);
                ps.executeUpdate();
            } catch (SQLException ex) {
                logger.warning("Database error (delete message): " + ex);
            }
        }, executorService);
    }
}
