package org.fletchly.genius.client.ollama;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class OllamaOptions {
    private double temperature;
    @JsonProperty("top_k")
    private int topK;
    @JsonProperty("top_p")
    private double topP;
    @JsonProperty("num_predict")
    private int numPredict;
}
