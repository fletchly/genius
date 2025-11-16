package org.fletchly.genius.ollama.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record OllamaResponse(
        String model,
        String createdAt,
        OllamaMessage message
) {
}
