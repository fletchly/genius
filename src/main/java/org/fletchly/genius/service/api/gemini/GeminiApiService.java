package org.fletchly.genius.service.api.gemini;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.fletchly.genius.service.api.ApiService;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

/**
 * Google Gemini API service implementation.
 */
public class GeminiApiService implements ApiService
{
    private final HttpClient httpClient;
    private final String apiKey;
    private final String baseUrl;
    private final int maxTokens;
    private final String systemContext;

    /**
     * Constructor for GeminiApiService.
     * @param apiKey the API key for authentication
     * @param baseUrl the base URL for the API
     * @param maxTokens the maximum number of tokens for the response
     * @param systemContext the system context for the API
     */
    public GeminiApiService(String apiKey, String baseUrl, int maxTokens, String systemContext)
    {
        this.httpClient = HttpClient.newHttpClient();
        this.apiKey = apiKey;
        this.baseUrl = baseUrl;
        this.maxTokens = maxTokens;
        this.systemContext = systemContext;
    }

    @Override
    public CompletableFuture<String> getResponse(String prompt) throws InterruptedException
    {
        // Create the request URL with the API key
        var urlWithKey = baseUrl + "?key=" + apiKey;

        // Create the request body
        var requestBody = new GeminiApiRequest(systemContext, prompt, maxTokens);

        // Create the HTTP request
        var request = HttpRequest.newBuilder()
                .uri(URI.create(urlWithKey))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody.toJson()))
                .build();

        // Send the request and handle the response asynchronously
        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() != 200)
                    {
                        throw new RuntimeException("Gemini API returned status code " + response.statusCode());
                    }
                    JsonElement root = JsonParser.parseString(response.body());
                    return root
                            .getAsJsonObject()
                            .getAsJsonArray("candidates")
                            .get(0).getAsJsonObject()
                            .getAsJsonObject("content")
                            .getAsJsonArray("parts")
                            .get(0)
                            .getAsJsonObject()
                            .get("text")
                            .getAsString()
                            .replaceFirst("\\r?\\n$", "");
                });
    }
}
