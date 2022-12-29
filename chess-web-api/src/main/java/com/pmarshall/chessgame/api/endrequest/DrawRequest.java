package com.pmarshall.chessgame.api.endrequest;

import com.pmarshall.chessgame.api.Message;
import java.util.Objects;

public record DrawRequest(
        Action action
) implements Message {

    public DrawRequest {
        Objects.requireNonNull(action);
    }

    public enum Action {
        PROPOSE,
        ACCEPT,
        REJECT
    }
}
