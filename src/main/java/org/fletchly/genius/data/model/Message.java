package org.fletchly.genius.data.model;

import lombok.Builder;
import lombok.Getter;

/**
 * @param role "user" | "assistant" | "tool"
 */
@Builder
public record Message(int id, int conversationId, String role, String content, long timestamp) {
    public enum Role {
        USER("user"),
        TOOL("tool"),
        ASSISTANT("assistant"),
        SYSTEM("system");

        @Getter
        private final String role;

        Role(String role) {
            this.role = role;
        }
    }
}
