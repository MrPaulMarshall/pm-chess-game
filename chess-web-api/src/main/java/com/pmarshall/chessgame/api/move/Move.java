package com.pmarshall.chessgame.api.move;

import com.pmarshall.chessgame.api.Message;
import com.pmarshall.chessgame.model.properties.PieceType;
import com.pmarshall.chessgame.model.properties.Position;

import java.util.Objects;

public record Move(
        Position from,
        Position to,
        PieceType promotion // null means no promotion
) implements Message {

    public Move {
        Objects.requireNonNull(from);
        Objects.requireNonNull(to);
    }
}
