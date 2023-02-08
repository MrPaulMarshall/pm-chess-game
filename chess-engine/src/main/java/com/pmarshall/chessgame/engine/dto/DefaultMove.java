package com.pmarshall.chessgame.engine.dto;

import com.pmarshall.chessgame.engine.properties.Position;

import java.util.Objects;

public record DefaultMove(
        Position from,
        Position to,
        boolean check,
        String notation
) implements LegalMove {

    public DefaultMove {
        Objects.requireNonNull(from);
        Objects.requireNonNull(to);
        Objects.requireNonNull(notation);
    }
}
