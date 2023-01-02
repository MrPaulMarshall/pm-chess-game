package com.pmarshall.chessgame.api.lobby;

import com.pmarshall.chessgame.api.Message;
import com.pmarshall.chessgame.model.properties.Color;
import com.pmarshall.chessgame.model.properties.Position;
import org.apache.commons.lang3.tuple.Triple;

import java.util.Collection;
import java.util.Objects;

public record MatchFound(
        Color color,
        String opponentId,
        Collection<Triple<Position, Position, Boolean>> legalMoves
) implements Message {

    public MatchFound {
        Objects.requireNonNull(opponentId);
        Objects.requireNonNull(color);
    }
}
