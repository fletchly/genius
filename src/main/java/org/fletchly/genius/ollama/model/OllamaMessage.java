package org.fletchly.genius.ollama.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import org.fletchly.genius.common.Message;
import org.jetbrains.annotations.NotNull;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record OllamaMessage(
        @NotNull
        String role,
        @NotNull
        String content
) implements Message {
}
