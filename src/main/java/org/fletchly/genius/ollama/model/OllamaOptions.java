package org.fletchly.genius.ollama.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record OllamaOptions(
        double temperature,
        @JsonProperty("top_k")
        int topK,
        @JsonProperty("top_p")
        double topP,
        @JsonProperty("num_predict")
        int numPredict
) {
}
