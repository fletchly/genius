package org.fletchly.genius.service.api.openai;

import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OpenAiApiServiceTests
{
    private OpenAiApiService api;

    @BeforeEach
    void setUp()
    {
        var dotenv = Dotenv.configure().filename("test.env").load();

        String apiKey = dotenv.get("API_KEY");
        String model = dotenv.get("MODEL");
        String baseUrl = dotenv.get("BASE_URL");
        String instructions = dotenv.get("SYSTEM_CONTEXT");
        int maxTokens = Integer.parseInt(dotenv.get("MAX_TOKENS"));

        api = new OpenAiApiService(apiKey, baseUrl, model, instructions, maxTokens);
    }

    @Test
    void testGetResponse() {
        var expectedResponse = "It works!";
        var response = api.getResponse("Repeat after me exactly: It works!");

        assertEquals(expectedResponse, response);
    }
}
