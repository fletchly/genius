package org.fletchly.genius.ollama.client.exceptions;

import lombok.Getter;

public class OllamaClientHttpException extends OllamaClientException {
    @Getter
    private final int statusCode;

    public OllamaClientHttpException(String message, Throwable cause, int statusCode) {
        super(message, cause);
        this.statusCode = statusCode;
    }
}
