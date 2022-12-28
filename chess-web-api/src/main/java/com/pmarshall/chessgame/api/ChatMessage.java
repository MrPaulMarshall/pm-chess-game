package com.pmarshall.chessgame.api;

import java.util.Objects;

public record ChatMessage(String text) implements Message {

    public ChatMessage {
        Objects.requireNonNull(text);
    }
}
