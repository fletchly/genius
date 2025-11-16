package org.fletchly.genius.ollama.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Singular;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record OllamaRequest(
        @NotNull
        String model,
        @NotNull
        @Singular
        List<OllamaMessage> messages,
        @NotNull
        OllamaOptions options,
        boolean stream,
        boolean think
) {
}
