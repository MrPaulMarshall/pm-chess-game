package com.pmarshall.chessgame.model.dto;

import com.pmarshall.chessgame.model.properties.MoveEffect;
import com.pmarshall.chessgame.model.properties.PieceType;
import com.pmarshall.chessgame.model.properties.Position;

import java.util.Objects;

public record Promotion(
        Position from,
        Position to,
        PieceType newType,
        MoveEffect moveEffect,
        String notation
) implements LegalMove {

    public Promotion {
        Objects.requireNonNull(from);
        Objects.requireNonNull(to);
        Objects.requireNonNull(newType);
        Objects.requireNonNull(moveEffect);
        Objects.requireNonNull(notation);
    }
}
