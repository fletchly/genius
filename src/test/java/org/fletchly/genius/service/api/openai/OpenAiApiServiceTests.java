package org.fletchly.genius.service.api.openai;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OpenAiApiServiceTests
{
    private OpenAiApiService api;

    @BeforeEach
    void setUp()
    {
        String apiKey = System.getenv("API_KEY");
        String model = System.getenv("MODEL");
        String baseUrl = System.getenv("BASE_URL");
        String instructions = System.getenv("SYSTEM_CONTEXT");
        int maxTokens = Integer.parseInt(System.getenv("MAX_TOKENS"));

        api = new OpenAiApiService(apiKey, baseUrl, model, instructions, maxTokens);
    }

    @Test
    void testGetResponse() {
        var expectedResponse = "It works!";
        var response = api.getResponse("Repeat after me exactly: It works!");

        assertEquals(expectedResponse, response);
    }
}
