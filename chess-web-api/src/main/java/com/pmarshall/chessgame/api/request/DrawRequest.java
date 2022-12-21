package com.pmarshall.chessgame.api.request;

import com.pmarshall.chessgame.api.Message;

import java.util.Objects;

public record DrawRequest(
        String gameSessionId,
        String proponentId
) implements Message {

    public DrawRequest {
        Objects.requireNonNull(gameSessionId);
        Objects.requireNonNull(proponentId);
    }
}
