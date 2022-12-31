package com.pmarshall.chessgame.api.move.request;

import com.pmarshall.chessgame.model.properties.PieceType;
import com.pmarshall.chessgame.model.properties.Position;

public record Promotion(
        Position from,
        Position to,
        PieceType decision
) implements MoveRequest {
}