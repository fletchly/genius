package org.fletchly.genius.data.model;

import lombok.Builder;

/**
 * @param role "user" | "assistant" | "tool"
 */
@Builder
public record Message(int id, int conversationId, String role, String content, long timestamp) {
}
