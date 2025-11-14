package org.fletchly.genius.client.ollama;

import lombok.*;

import java.util.List;

@Builder @Getter @Setter @AllArgsConstructor
public class OllamaRequest {
    String model;
    @Singular
    List<OllamaMessage> messages;
    OllamaOptions options;
    @Builder.Default
    boolean think = false;
    @Builder.Default
    boolean stream = false;
}
