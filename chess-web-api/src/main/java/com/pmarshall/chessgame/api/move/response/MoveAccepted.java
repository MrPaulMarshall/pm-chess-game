package com.pmarshall.chessgame.api.move;

public record MoveAccepted(
        boolean promotionRequired
) implements MoveResponse {
}
