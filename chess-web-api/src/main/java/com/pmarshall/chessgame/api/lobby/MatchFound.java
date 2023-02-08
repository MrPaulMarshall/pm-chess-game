package com.pmarshall.chessgame.api.lobby;

import com.pmarshall.chessgame.api.Message;
import com.pmarshall.chessgame.engine.dto.LegalMove;
import com.pmarshall.chessgame.engine.properties.Color;

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
