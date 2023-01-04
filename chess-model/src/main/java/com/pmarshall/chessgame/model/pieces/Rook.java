package com.pmarshall.chessgame.model.pieces;

import com.pmarshall.chessgame.model.game.InMemoryChessGame;
import com.pmarshall.chessgame.model.properties.Color;
import com.pmarshall.chessgame.model.properties.PieceType;

/**
 * @author Paweł Marszał
 *
 * Extends abstract class Piece
 */
public final class Rook extends Piece {
    // directions: up, right, down, left
    static private final int[][] moveDirections = {
            {0, 1},
            {1, 0},
            {0, -1},
            {-1, 0}
    };

    public Rook(Color color) {
        super(color);
    }

    @Override
    public void updateMovesWithoutProtectingKing(InMemoryChessGame game) {
        movesWithoutProtectingKing.clear();
        movesWithoutProtectingKing.addAll(unlimitedMovesInGivenDirections(game, moveDirections));
    }

    @Override
    public PieceType getType() {
        return PieceType.ROOK;
    }

    @Override
    public String toString() {
        return "R";
    }
}
