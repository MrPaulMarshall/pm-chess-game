package com.pmarshall.chessgame.api.endrequest;

import com.pmarshall.chessgame.api.Message;
import java.util.Objects;

public record DrawRequest(
        Action action
) implements Message {

    public DrawRequest {
        Objects.requireNonNull(action);
    }

    // TODO: consider splitting into two message types - request/response?
    public enum Action {
        PROPOSE,
        ALREADY_PROPOSED,
        ACCEPT,
        REJECT
    }
}
