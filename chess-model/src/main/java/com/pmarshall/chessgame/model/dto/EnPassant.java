package com.pmarshall.chessgame.model.dto;

import com.pmarshall.chessgame.model.properties.Position;

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
