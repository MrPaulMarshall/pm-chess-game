package com.pmarshall.chessgame.api.move.response;

public record MoveAccepted(
        boolean promotionRequired
) implements MoveResponse {
}
