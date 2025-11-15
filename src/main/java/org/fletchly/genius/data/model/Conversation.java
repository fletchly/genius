package org.fletchly.genius.data.model;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class Conversation {
    private int id;
    private UUID playerUuid;
    private long created;
    private long updated;
}
