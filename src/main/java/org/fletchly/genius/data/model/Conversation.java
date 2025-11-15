package org.fletchly.genius.data.model;

import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record Conversation(int id, UUID playerUuid, long created, long updated, List<Message> messages) {
}
