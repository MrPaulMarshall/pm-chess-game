package com.pmarshall.chessgame.api.move.request;

import com.pmarshall.chessgame.model.properties.Position;

public record Move(
        Position from,
        Position to
) implements MoveRequest {
}
