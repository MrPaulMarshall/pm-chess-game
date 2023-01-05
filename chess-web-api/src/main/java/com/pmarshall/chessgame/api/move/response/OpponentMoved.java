package com.pmarshall.chessgame.api.move.response;

import com.pmarshall.chessgame.api.Message;
import com.pmarshall.chessgame.model.api.LegalMove;
import com.pmarshall.chessgame.model.properties.PieceType;
import com.pmarshall.chessgame.model.properties.Position;

import java.util.List;

public record OpponentMoved(
        Position from,
        Position to,
        PieceType promotion,
        String moveRepresentation,
        boolean check,
        List<LegalMove> legalMoves
) implements Message {
}
