package com.pmarshall.chessgame.api.lobby;

import com.pmarshall.chessgame.api.Message;
import com.pmarshall.chessgame.model.dto.LegalMove;
import com.pmarshall.chessgame.model.properties.Color;

import java.util.List;
import java.util.Objects;

public record MatchFound(
        Color color,
        String opponentName,
        List<LegalMove> legalMoves
) implements Message {

    public MatchFound {
        Objects.requireNonNull(opponentName);
        Objects.requireNonNull(color);
        Objects.requireNonNull(legalMoves);
    }
}
