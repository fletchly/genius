package org.fletchly.genius.ollama.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import org.fletchly.genius.common.Message;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record OllamaResponse(
        String model,
        String createdAt,
        OllamaMessage message
) {
}
