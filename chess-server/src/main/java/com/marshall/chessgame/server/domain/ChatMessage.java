package com.marshall.chessgame.server.chat;

import java.util.Objects;

public record ChatMessage(
        String playerId,
        String message
) {

    public ChatMessage {
        Objects.requireNonNull(playerId);
        Objects.requireNonNull(message);
    }
}
