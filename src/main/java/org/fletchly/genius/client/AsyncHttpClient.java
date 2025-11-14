package org.fletchly.genius.client;

import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

/**
 * Abstract class providing functionality for making asynchronous HTTP requests
 * using OkHttpClient. It wraps the execution of HTTP requests in CompletableFuture
 * to allow non-blocking operations and easier handling of asynchronous results.
 */
public abstract class AsyncHttpClient {
    private final OkHttpClient client = new OkHttpClient();

    /**
     * Executes the given HTTP request asynchronously, returning a CompletableFuture
     * that is completed with the response or an exception if an error occurs.
     *
     * @param request the HTTP request to be executed asynchronously, must not be null.
     * @return a CompletableFuture that will be completed with the HTTP response
     *         or exceptionally completed with an error if the request fails.
     */
    protected CompletableFuture<Response> executeAsync(@NotNull Request request) {
        CompletableFuture<Response> responseFuture = new CompletableFuture<>();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                responseFuture.completeExceptionally(e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    responseFuture.completeExceptionally(new IOException("Unexpected code " + response));
                }

                responseFuture.complete(response);
            }
        });

        return responseFuture;
    }

    protected void closeClient() {
        client.dispatcher().executorService().close();
        client.dispatcher().cancelAll();
        client.connectionPool().evictAll();
    }
}
