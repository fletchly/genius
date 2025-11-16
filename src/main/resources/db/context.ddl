CREATE SCHEMA IF NOT EXISTS context;

CREATE TABLE IF NOT EXISTS context.conversations
(
    id          INTEGER PRIMARY KEY AUTO_INCREMENT,
    player_uuid TEXT    NOT NULL,
    created_at  INTEGER NOT NULL,
    updated_at  INTEGER NOT NULL
);

CREATE TABLE IF NOT EXISTS context.messages
(
    id              INTEGER PRIMARY KEY AUTO_INCREMENT,
    conversation_id INTEGER NOT NULL,
    role            TEXT    NOT NULL,
    content         TEXT    NOT NULL,
    `timestamp`     INTEGER NOT NULL,
    FOREIGN KEY (conversation_id) REFERENCES context.conversations (id)
);

CREATE INDEX idx_conversations_player_uuid ON context.conversations (player_uuid);
CREATE INDEX idx_messages_conversation_id ON context.messages (conversation_id);
CREATE INDEX idx_messages_timestamp ON context.messages (`timestamp`);
