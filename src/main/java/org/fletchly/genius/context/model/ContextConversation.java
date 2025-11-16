package org.fletchly.genius.context.model;

import lombok.Builder;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

@Builder
public record ContextConversation(
        long id,
        @NotNull
        UUID playerUuid,
        long createdAt,
        long updatedAt,
        @NotNull
        List<ContextMessage> messages
) {
}
