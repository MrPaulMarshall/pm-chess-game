package com.pmarshall.chessgame.model.api;

import com.pmarshall.chessgame.model.properties.Position;

import java.util.Objects;

public record LegalMove(Position from, Position to, boolean promotion) {

    public LegalMove {
        Objects.requireNonNull(from);
        Objects.requireNonNull(to);
    }
}
