package com.pmarshall.chessgame.model.dto;

import com.pmarshall.chessgame.model.properties.PieceType;
import com.pmarshall.chessgame.model.properties.Position;

import java.util.Map;
import java.util.Objects;

public record Promotion(Position from, Position to, Map<PieceType, Boolean> checksByPickedPiece) implements LegalMove {

    public Promotion {
        Objects.requireNonNull(from);
        Objects.requireNonNull(to);
        Objects.requireNonNull(checksByPickedPiece);
        checksByPickedPiece = Map.copyOf(checksByPickedPiece);
    }
}
