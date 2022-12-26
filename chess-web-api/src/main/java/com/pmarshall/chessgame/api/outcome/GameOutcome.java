package com.pmarshall.chessgame.api.outcome;

import com.pmarshall.chessgame.api.Message;
import java.util.Objects;

public record GameOutcome(
        Type outcome,
        String message
) implements Message {

    public GameOutcome {
        Objects.requireNonNull(outcome);
    }

    public enum Type {
        VICTORY,
        DEFEAT,
        DRAW
    }
}
