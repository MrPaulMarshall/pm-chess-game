package com.pmarshall.chessgame.engine.pieces;

import com.pmarshall.chessgame.engine.game.InMemoryChessGame;
import com.pmarshall.chessgame.engine.properties.Color;
import com.pmarshall.chessgame.engine.properties.PieceType;

/**
 * @author Paweł Marszał
 *
 * Extends abstract class Piece
 */
public final class Queen extends Piece {
    // directions: all directions
    static private final int[][] moveDirections = {
            {-1, 1},
            {0, 1},
            {1, 1},
            {1, 0},
            {1, -1},
            {0, -1},
            {-1, -1},
            {-1, 0}
    };

    public Queen(Color color) {
        super(color);
    }

    @Override
    public void updateMovesWithoutProtectingKing(InMemoryChessGame game) {
        movesWithoutProtectingKing.clear();
        movesWithoutProtectingKing.addAll(unlimitedMovesInGivenDirections(game, moveDirections));
    }

    @Override
    public PieceType getType() {
        return PieceType.QUEEN;
    }

    @Override
    public String toString() {
        return "Q";
    }
}
