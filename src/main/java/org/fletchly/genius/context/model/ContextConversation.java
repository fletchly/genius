package org.fletchly.genius.context.model;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record ContextConversation(
        long id,
        @NotNull
        UUID playerUuid,
        long createdAt,
        long updatedAt
) {
}
