package com.pmarshall.chessgame.api.request;

import com.pmarshall.chessgame.api.Message;

import java.util.Objects;

public record SurrenderRequest(
        String gameSessionId,
        String playerId
) implements Message {

    public SurrenderRequest {
        Objects.requireNonNull(gameSessionId);
        Objects.requireNonNull(playerId);
    }
}
