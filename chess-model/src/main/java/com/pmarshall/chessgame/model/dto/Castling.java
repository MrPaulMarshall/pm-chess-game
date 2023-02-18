package com.pmarshall.chessgame.model.dto;

import com.pmarshall.chessgame.model.properties.MoveEffect;
import com.pmarshall.chessgame.model.properties.Position;

import java.util.Objects;

public record Castling(
        Position from,
        Position to,
        boolean queenSide,
        MoveEffect moveEffect,
        String notation
) implements LegalMove {

    public Castling {
        Objects.requireNonNull(from);
        Objects.requireNonNull(to);
        Objects.requireNonNull(moveEffect);
        Objects.requireNonNull(notation);
    }
}
