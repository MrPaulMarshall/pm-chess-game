package com.pmarshall.chessgame.model.properties;

public enum PieceType {
    KING("K"),
    QUEEN("Q"),
    ROOK("R"),
    BISHOP("B"),
    KNIGHT("N"),
    PAWN("");

    private final String code;

    PieceType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
