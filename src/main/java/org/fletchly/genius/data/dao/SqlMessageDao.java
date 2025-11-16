package org.fletchly.genius.data.dao;

import org.fletchly.genius.data.Database;
import org.fletchly.genius.data.model.Message;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public class SqlMessageDao implements MessageDao {
    private final Database db;
    private final ExecutorService executor;

    public SqlMessageDao(Database db, ExecutorService executor) {
        this.db = db;
        this.executor = executor;
    }

    @Override
    public CompletableFuture<List<Message>> getMessagesForConversation(int conversationId) {
        return CompletableFuture.supplyAsync(() -> {
            try (PreparedStatement ps = db.getConnection().prepareStatement(
                    "SELECT id, role, content, `timestamp` FROM context.messages WHERE conversation_id = ? ORDER BY `timestamp`;"
            )) {
                ps.setInt(1, conversationId);
                ResultSet rs = ps.executeQuery();
                List<Message> messages = new ArrayList<>();
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String role = rs.getString("role");
                    String content = rs.getString("content");
                    long timestamp = rs.getLong("timestamp"); // FIXME: Maybe?
                    messages.add(Message.builder()
                            .id(id)
                            .conversationId(conversationId)
                            .role(role)
                            .content(content)
                            .timestamp(timestamp)
                            .build());
                }
                return messages;
            } catch (SQLException e) {
                throw new DaoException("Error getting messages for conversation id=" + conversationId, e);
            }
        }, executor);
    }

    @Override
    public CompletableFuture<Void> addMessage(int conversationId, String role, String content) {
        long now = System.currentTimeMillis();
        return CompletableFuture.runAsync(() -> {
            try (PreparedStatement ps = db.getConnection().prepareStatement("INSERT INTO context.messages(conversation_id, role, content, `timestamp`) VALUES (?, ?, ?, ?);"
            )) {
                ps.setInt(1, conversationId);
                ps.setString(2, role);
                ps.setString(3, content);
                ps.setLong(4, now);
                ps.executeUpdate();
            } catch (SQLException e) {
                throw new DaoException(String.format("Error inserting message (conversation_id=%d, role=%s, content=%s, timestamp=%d)", conversationId, role, content, now), e);
            }
        }, executor);
    }

    @Override
    public CompletableFuture<Void> deleteMessages(int conversationId) {
        return CompletableFuture.runAsync(() -> {
            try (PreparedStatement ps = db.getConnection().prepareStatement(
                    "DELETE FROM context.messages WHERE conversation_id = ?;"
            )) {
                ps.setInt(1, conversationId);
                ps.executeUpdate();
            } catch (SQLException e) {
                throw new DaoException("Error deleting conversation id=" + conversationId, e);
            }
        }, executor);
    }
}
