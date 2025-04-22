package org.fletchly.genius.service.api;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

/**
 * ApiService interface for handling API requests.
 */
public interface ApiService
{
    /**
     * Get a response from the API based on the provided prompt.
     * @param prompt the prompt to send to the API
     * @return the response from the API
     * @throws IOException if an I/O error occurs
     */
    public CompletableFuture<String> getResponse(String prompt) throws InterruptedException;
}
