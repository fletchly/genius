package org.fletchly.genius.client.ollama;

import lombok.*;

@Builder @Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class OllamaMessage {
    String role;
    String content;
}
