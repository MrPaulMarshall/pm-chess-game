package com.pmarshall.chessgame.model.pieces;

import com.pmarshall.chessgame.model.properties.Color;
import com.pmarshall.chessgame.model.game.Game;

/**
 * @author Paweł Marszał
 *
 * Extends abstract class Piece
 */
public class Queen extends Piece {
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
    public void updateMovesWithoutProtectingKing(Game game) {
        movesWithoutProtectingKing.clear();
        movesWithoutProtectingKing.addAll(unlimitedMovesInGivenDirections(game, moveDirections));
    }

    @Override
    public String toString() {
        return "Q";
    }
}
