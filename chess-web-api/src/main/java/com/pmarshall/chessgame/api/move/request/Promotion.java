package com.pmarshall.chessgame.api.move.request;

import com.pmarshall.chessgame.model.properties.PieceType;
import com.pmarshall.chessgame.model.properties.Position;

import java.util.Objects;

public record Promotion(
        Position from,
        Position to,
        PieceType decision
) implements MoveRequest {

    public Promotion {
        Objects.requireNonNull(from);
        Objects.requireNonNull(to);
        Objects.requireNonNull(decision);
    }
}
