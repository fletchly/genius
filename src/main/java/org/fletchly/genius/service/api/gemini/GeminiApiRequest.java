package org.fletchly.genius.service.api.gemini;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.fletchly.genius.service.api.ApiRequest;

import java.util.List;

/**
 * Represents a request to the Gemini API.
 */
public class GeminiApiRequest implements ApiRequest
{
    @SerializedName("system_instructions")
    private PartList systemInstructions;

    @SerializedName("contents")
    private PartList contents;

    @SerializedName("generationConfig")
    private GenerationConfig generationConfig;

    /**
     * Constructor for GeminiApiRequest.
     *
     * @param systemContext the system context for the API
     * @param prompt        the prompt to send to the API
     * @param maxTokens     the maximum number of tokens for the response
     */
    public GeminiApiRequest(String systemContext, String prompt, int maxTokens)
    {
        this.systemInstructions = new PartList(List.of(new Part(systemContext)));
        this.contents = new PartList(List.of(new Part(prompt)));
        this.generationConfig = new GenerationConfig(maxTokens);
    }

    @Override
    public String toJson()
    {
        var gson = new Gson();
        return gson.toJson(this);
    }

    @Setter
    @Getter
    @AllArgsConstructor
    public static class Part
    {
        private String text;
    }

    @Setter
    @Getter
    @AllArgsConstructor
    public static class PartList
    {
        private List<Part> parts;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class GenerationConfig
    {
        private int maxOutputTokens;
    }
}
