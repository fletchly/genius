package org.fletchly.genius.context.model;

import lombok.Builder;
import org.fletchly.genius.common.Message;
import org.jetbrains.annotations.NotNull;

@Builder
public record ContextMessage(
        long id,
        long conversationId,
        @NotNull
        String role,
        @NotNull
        String content,
        long createdAt
) implements Message {
}
