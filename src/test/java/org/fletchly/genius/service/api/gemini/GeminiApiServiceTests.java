package org.fletchly.genius.service.api.gemini;

import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GeminiApiServiceTests
{
    private String apiKey;
    private String baseUrl;
    private String systemContext;

    @BeforeEach
    void setUp() {
        Dotenv dotenv = Dotenv.configure().filename("test.env").load();
        apiKey = dotenv.get("GEMINI_API_KEY");
        baseUrl = dotenv.get("GEMINI_BASE_URL");
        systemContext = dotenv.get("SYSTEM_CONTEXT");
    }

    @Test
    void testGetResponse() throws InterruptedException
    {
        int maxTokens = 200;
        var apiService = new GeminiApiService(apiKey, baseUrl, maxTokens, systemContext);
        var expectedResponse = "It works!";
        var response = apiService.getResponse("Say the following exactly: It works!").join();
        assertEquals(expectedResponse, response);
    }
}
