package com.pmarshall.chessgame.api.move;

import com.pmarshall.chessgame.api.Message;
import com.pmarshall.chessgame.model.dto.LegalMove;
import com.pmarshall.chessgame.model.properties.PieceType;
import com.pmarshall.chessgame.model.properties.Position;

import java.util.List;

public record OpponentMoved(
        Position from,
        Position to,
        PieceType promotion,
        boolean withCheck,
        String moveRepresentation,
        List<LegalMove> legalMoves
) implements Message {
}
