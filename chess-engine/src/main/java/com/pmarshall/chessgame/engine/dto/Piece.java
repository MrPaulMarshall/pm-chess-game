package com.pmarshall.chessgame.engine.dto;

import com.pmarshall.chessgame.engine.properties.Color;
import com.pmarshall.chessgame.engine.properties.PieceType;

import java.util.Objects;

public record Piece(PieceType piece, Color color) {

    public Piece {
        Objects.requireNonNull(piece);
        Objects.requireNonNull(color);
    }
}
