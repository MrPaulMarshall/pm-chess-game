package com.pmarshall.chessgame.api.move;

import com.pmarshall.chessgame.model.properties.Position;

public record MoveRequest(
        Position startingPosition,
        Position finalPosition
) implements Move {
}
