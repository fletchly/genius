package org.fletchly.genius._ollama.model;

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
