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
}
