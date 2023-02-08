package com.pmarshall.chessgame.engine.dto;

import com.pmarshall.chessgame.engine.properties.PieceType;
import com.pmarshall.chessgame.engine.properties.Position;

import java.util.Objects;

public record Promotion(
        Position from,
        Position to,
        PieceType newType,
        boolean check,
        String notation
) implements LegalMove {

    public Promotion {
        Objects.requireNonNull(from);
        Objects.requireNonNull(to);
        Objects.requireNonNull(newType);
        Objects.requireNonNull(notation);
    }
}
