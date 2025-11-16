package org.fletchly.genius.common;

import org.jetbrains.annotations.NotNull;

public record PlainMessage(@NotNull String role, @NotNull String content) implements Message {
}
