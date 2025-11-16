package org.fletchly.genius.ollama.client.exceptions;

public class OllamaClientException extends RuntimeException {
    public OllamaClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
