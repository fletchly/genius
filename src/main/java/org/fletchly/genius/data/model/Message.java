package org.fletchly.genius.data.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Message {
    private int id;
    private int conversationId;
    private String role; // "user" | "assistant" | "tool"
    private String content;
    private long timestamp;
}
