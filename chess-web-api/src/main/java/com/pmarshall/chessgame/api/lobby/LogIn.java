package com.pmarshall.chessgame.api.lobby;

import com.pmarshall.chessgame.api.Message;

import java.util.Objects;

public record LogIn(String name) implements Message {

    public LogIn {
        Objects.requireNonNull(name);
    }
}
