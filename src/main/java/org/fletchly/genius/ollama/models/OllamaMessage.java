package org.fletchly.genius.ollama.models;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OllamaMessage {
    String role;
    String content;
}
