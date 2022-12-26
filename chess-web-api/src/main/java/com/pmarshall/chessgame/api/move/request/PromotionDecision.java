package com.pmarshall.chessgame.api.move.request;

import com.pmarshall.chessgame.model.pieces.PieceType;

public record PromotionDecision(
        PieceType decision
) implements MoveRequest {
}
