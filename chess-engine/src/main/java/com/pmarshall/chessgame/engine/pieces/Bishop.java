package com.pmarshall.chessgame.engine.pieces;

import com.pmarshall.chessgame.engine.properties.Color;
import com.pmarshall.chessgame.engine.game.InMemoryChessGame;
import com.pmarshall.chessgame.engine.properties.PieceType;

/**
 * @author Paweł Marszał
 *
 * Extends abstract class Piece
 */
public final class Bishop extends Piece {
    // directions: left-up, right-up, right-down, left-down
    static private final int[][] moveDirections = {
            {-1, 1},
            {1, 1},
            {1, -1},
            {-1, -1}
    };

    public Bishop(Color color) {
        super(color);
    }

    @Override
    public void updateMovesWithoutProtectingKing(InMemoryChessGame game) {
        movesWithoutProtectingKing.clear();
        movesWithoutProtectingKing.addAll(unlimitedMovesInGivenDirections(game, moveDirections));
    }

    @Override
    public PieceType getType() {
        return PieceType.BISHOP;
    }

    @Override
    public String toString() {
        return "B";
    }
}
