package com.pmarshall.chessgame.api.lobby;

import com.pmarshall.chessgame.api.Message;

import java.util.Objects;

public record AssignId(
        String id
) implements Message {

    public AssignId {
        Objects.requireNonNull(id);
    }
}
