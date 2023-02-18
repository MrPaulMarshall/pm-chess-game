package com.pmarshall.chessgame.model.dto;

import com.pmarshall.chessgame.model.properties.MoveEffect;
import com.pmarshall.chessgame.model.properties.Position;

import java.util.Objects;

public record EnPassant(
        Position from,
        Position to,
        Position takenPawn,
        MoveEffect moveEffect,
        String notation
) implements LegalMove {

    public EnPassant {
        Objects.requireNonNull(from);
        Objects.requireNonNull(to);
        Objects.requireNonNull(takenPawn);
        Objects.requireNonNull(moveEffect);
        Objects.requireNonNull(notation);
    }
}
