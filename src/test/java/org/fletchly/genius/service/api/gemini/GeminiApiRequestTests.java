package org.fletchly.genius.service.api.gemini;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GeminiApiRequestTests
{
    @Test
    void testToJson() {
        GeminiApiRequest geminiApiRequest = new GeminiApiRequest("context", "prompt", 1);
        String expectedJson = """
                {"system_instruction":{"parts":[{"text":"context"}]},"contents":[{"parts":[{"text":"prompt"}]}],"generationConfig":{"maxOutputTokens":1}}""";

        assertEquals(expectedJson, geminiApiRequest.toJson());
    }
}
