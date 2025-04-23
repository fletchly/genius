package org.fletchly.genius.service.api.openai;

import com.openai.client.OpenAIClientAsync;
import com.openai.client.okhttp.OpenAIOkHttpClientAsync;
import com.openai.models.chat.completions.ChatCompletionCreateParams;

public class OpenAiApiService
{
    private final OpenAIClientAsync client;
    private final String model;
    private final String instructions;
    private final int maxOutputTokens;

    public OpenAiApiService(String apiKey, String baseUrl, String model, String instructions, int maxOutputTokens)
    {
        this.maxOutputTokens = maxOutputTokens;
        this.instructions = instructions;
        this.model = model;
        client = OpenAIOkHttpClientAsync.builder()
                .apiKey(apiKey)
                .baseUrl(baseUrl)
                .build();
    }

    public String getResponse(String prompt)
    {
        ChatCompletionCreateParams completionParams = ChatCompletionCreateParams.builder()
                .model(model)
                .maxCompletionTokens(maxOutputTokens)
                .addDeveloperMessage(instructions)
                .addUserMessage(prompt)
                .build();

        var response = client.chat()
                .completions()
                .create(completionParams)
                .thenApply(completion -> completion.choices().stream()
                        .flatMap(choice -> choice.message().content().stream())
                        .toList()
                        .getFirst()
                ).join();

        client.close();

        return response.replaceFirst("\\r?\\n$", "");
    }
}
