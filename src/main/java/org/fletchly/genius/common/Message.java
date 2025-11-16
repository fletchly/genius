package org.fletchly.genius.common;

public interface Message {
    String role();

    String content();

    enum Role {
        USER,
        ASSISTANT,
        TOOL,
        SYSTEM;

        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }

    final class Roles {
        public static final String USER = "user";
        public static final String ASSISTANT = "assistant";
        public static final String TOOL = "tool";
        public static final String SYSTEM = "system";
    }
}
