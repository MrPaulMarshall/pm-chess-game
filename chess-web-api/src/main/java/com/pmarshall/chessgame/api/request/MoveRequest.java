package com.pmarshall.chessgame.api.request;

import com.pmarshall.chessgame.api.Message;

import java.util.Objects;

public record MoveRequest(
        String gameSessionId,
        String playerId
) implements Message {

    public MoveRequest {
        Objects.requireNonNull(gameSessionId);
        Objects.requireNonNull(playerId);
    }
}
