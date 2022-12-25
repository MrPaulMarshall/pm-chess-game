package com.pmarshall.chessgame.api.move;

public record PromotionDecision(
        // TODO: change to enum
        String piece
) implements Move {
}
