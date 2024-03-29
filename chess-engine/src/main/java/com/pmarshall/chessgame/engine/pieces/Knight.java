package com.pmarshall.chessgame.engine.pieces;

import com.pmarshall.chessgame.model.properties.Color;
import com.pmarshall.chessgame.engine.game.InMemoryChessGame;
import com.pmarshall.chessgame.model.properties.PieceType;

/**
 * @author Paweł Marszał
 *
 * Extends abstract class Piece
 */
public final class Knight extends Piece {
    // all jumps, starting with 2-up--1-left and going clockwise
    static private final int[][] jumps = {
            {-1, 2},
            {1, 2},
            {2, 1},
            {2, -1},
            {1, -2},
            {-1, -2},
            {-2, -1},
            {-2, 1}
    };

    public Knight(Color color) {
        super(color);
    }

    @Override
    public void updateMovesWithoutProtectingKing(InMemoryChessGame game) {
        movesWithoutProtectingKing.clear();
        movesWithoutProtectingKing.addAll(movesViaGivenJumps(game, jumps));
    }

    @Override
    public PieceType getType() {
        return PieceType.KNIGHT;
    }

    @Override
    public String toString() {
        return "N";
    }
}
