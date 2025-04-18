package org.fletchly.genius.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Gemini API service class
 */
public class ApiService
{
    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private static final String BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent";
    private final String apiKey;
    private final String systemInstructions;
    private final String maxTokens;

    /**
     * Instantiate API service with configuration options
     * @param config Plugin config
     */
    public ApiService(FileConfiguration config)
    {
        this.apiKey = config.getString("gemini-config.api-key");
        this.systemInstructions = config.getString("gemini-config.system-instructions");
        this.maxTokens = config.getString("gemini-config.max-tokens");
    }

    /**
     * Get a response from Gemini
     * @param prompt User prompt
     * @return Gemini's response
     * @throws IOException if the API gives a response status other than 200.
     * @throws InterruptedException if communication with the server is interrupted.
     */
    public String getResponse(String prompt) throws IOException, InterruptedException
    {
        // Prepare JSON for the request body
        var requestJson = String.format("""
                    {
                    "system_instruction": {
                        "parts": [
                            {
                                "text": "%s"
                            }
                        ]
                    },
                    "contents": [
                        {
                            "parts": [
                                {
                                    "text": "%s"
                                }
                            ]
                        }
                    ],
                    "generationConfig": {
                        "max_output_tokens": %s
                    }
                }
                """, systemInstructions, prompt, maxTokens);

        // Add loaded API key to base URL
        var urlWithKey = BASE_URL + "?key=" + apiKey;

        // Create request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlWithKey))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestJson))
                .build();

        // Send request
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        // If the response status is anything other than 200, throw an exception.
        if (response.statusCode() != 200)
        {
            throw new IOException("Error: " + response.statusCode() + " - " + response.body());
        }

        // Map response
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(response.body());

        return root.path("candidates")
                .get(0)
                .path("content")
                .path("parts")
                .get(0)
                .path("text")
                .asText()
                .replaceFirst("\\r?\\n$", "");
    }
}

