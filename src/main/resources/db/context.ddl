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
