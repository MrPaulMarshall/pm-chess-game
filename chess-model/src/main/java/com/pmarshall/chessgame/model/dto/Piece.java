package com.pmarshall.chessgame.model.dto;

import com.pmarshall.chessgame.model.properties.Color;
import com.pmarshall.chessgame.model.properties.PieceType;

import java.util.Objects;

public record Piece(PieceType piece, Color color) {

    public Piece {
        Objects.requireNonNull(piece);
        Objects.requireNonNull(color);
    }
}
