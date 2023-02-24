package com.pmarshall.chessgame.api.outcome;

import com.pmarshall.chessgame.api.Message;
import java.util.Objects;

public record GameFinished(
        Type outcome,
        String message
) implements Message {

    public GameFinished {
        Objects.requireNonNull(outcome);
    }

    public enum Type {
        VICTORY,
        DEFEAT,
        DRAW
    }
}
