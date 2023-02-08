package com.pmarshall.chessgame.engine.dto;

import com.pmarshall.chessgame.engine.properties.Position;

import java.util.Objects;

public record EnPassant(
        Position from,
        Position to,
        Position takenPawn,
        boolean check,
        String notation
) implements LegalMove {

    public EnPassant {
        Objects.requireNonNull(from);
        Objects.requireNonNull(to);
        Objects.requireNonNull(takenPawn);
        Objects.requireNonNull(notation);
    }
}
