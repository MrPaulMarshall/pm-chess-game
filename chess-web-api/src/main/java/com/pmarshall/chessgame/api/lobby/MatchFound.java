package com.pmarshall.chessgame.api.lobby;

import com.pmarshall.chessgame.api.Message;
import com.pmarshall.chessgame.model.api.LegalMove;
import com.pmarshall.chessgame.model.properties.Color;

import java.util.List;
import java.util.Objects;

public record MatchFound(
        Color color,
        String opponentId,
        List<LegalMove> legalMoves
) implements Message {

    public MatchFound {
        Objects.requireNonNull(opponentId);
        Objects.requireNonNull(color);
        Objects.requireNonNull(legalMoves);
    }
}
