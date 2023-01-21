package com.pmarshall.chessgame.api.move;

import com.pmarshall.chessgame.api.Message;
import com.pmarshall.chessgame.model.dto.LegalMove;

import java.util.List;
import java.util.Objects;

public record OpponentMoved(
        LegalMove move,
        List<LegalMove> legalMoves
) implements Message {

    public OpponentMoved {
        Objects.requireNonNull(move);
        Objects.requireNonNull(legalMoves);
        legalMoves = List.copyOf(legalMoves);
    }
}
