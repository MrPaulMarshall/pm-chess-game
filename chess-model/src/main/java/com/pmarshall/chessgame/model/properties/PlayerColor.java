package com.pmarshall.chessgame.model.properties;

/**
 * @author Paweł Marszał
 *
 * Stores color of pieces, which are also identifiers of players
 */
public enum PlayerColor {
    WHITE, BLACK;

    public PlayerColor next() {
        return this == WHITE ? BLACK : WHITE;
    }

    @Override
    public String toString() {
        return this == WHITE ? "White" : "Black";
    }
}
