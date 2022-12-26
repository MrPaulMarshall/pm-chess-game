package com.pmarshall.chessgame.api.lobby;

import com.pmarshall.chessgame.api.Message;
import com.pmarshall.chessgame.model.properties.Color;

import java.util.Objects;

public record MatchFound(
        String opponentId,
        Color color
) implements Message {

    public MatchFound {
        Objects.requireNonNull(opponentId);
        Objects.requireNonNull(color);
    }
}
