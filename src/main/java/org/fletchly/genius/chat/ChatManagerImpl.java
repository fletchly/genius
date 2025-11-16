package org.fletchly.genius.chat;

import org.fletchly.genius.common.Message;
import org.fletchly.genius.ollama.OllamaService;
import org.fletchly.genius.ollama.client.exceptions.OllamaClientHttpException;
import org.fletchly.genius.ollama.client.exceptions.OllamaClientParseException;
import org.fletchly.genius.ollama.model.OllamaMessage;

import javax.inject.Inject;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

public class ChatManagerImpl implements ChatManager {
    private final OllamaService ollamaService;
    private final Logger logger;

    @Inject
    public ChatManagerImpl(OllamaService ollamaService, Logger logger) {
        this.ollamaService = ollamaService;
        this.logger = logger;
    }

    @Override
    public CompletableFuture<String> generateChat(UUID playerUuid, String prompt) {
        OllamaMessage ollamaMessage = OllamaMessage.builder()
                .role(Message.Roles.USER)
                .content(prompt)
                .build();

        return ollamaService.generateChat(ollamaMessage)
                .thenApply(Message::content)
                .exceptionally(throwable -> {
                    // Unwrap exception
                    Throwable cause = throwable.getCause() != null
                            ? throwable.getCause()
                            : throwable;

                    logger.warning(cause.getMessage());

                    if (cause instanceof OllamaClientHttpException) throw new ChatManagerException("Something unexpected happened while connecting to the server.", cause);

                    if (cause instanceof OllamaClientParseException) throw new ChatManagerException("Something unexpected happened while getting a response", cause);

                    throw new ChatManagerException("An unknown error occured", cause);
                });
    }

    public static class ChatManagerException extends RuntimeException {
        public ChatManagerException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
