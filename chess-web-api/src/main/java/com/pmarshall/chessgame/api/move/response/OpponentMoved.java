package com.pmarshall.chessgame.api.move.response;

import com.pmarshall.chessgame.api.Message;
import com.pmarshall.chessgame.api.move.request.MoveRequest;
import com.pmarshall.chessgame.model.properties.Position;

import java.util.List;

public record OpponentMoved(
        Position from,
        Position to,
        String moveRepresentation,
        boolean check,
        List<MoveRequest> possibleMoves
) implements Message {
}
