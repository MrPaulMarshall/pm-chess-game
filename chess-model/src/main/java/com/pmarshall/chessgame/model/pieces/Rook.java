package com.pmarshall.chessgame.model.pieces;

import com.pmarshall.chessgame.model.properties.PlayerColor;
import com.pmarshall.chessgame.model.game.Game;

/**
 * @author Paweł Marszał
 *
 * Extends abstract class Piece
 */
public class Rook extends Piece {
    // directions: up, right, down, left
    static private final int[][] moveDirections = {
            {0, 1},
            {1, 0},
            {0, -1},
            {-1, 0}
    };

    public Rook(PlayerColor playerColor) {
        super(playerColor);
    }

    @Override
    public void updateMovesWithoutProtectingKing(Game game) {
        movesWithoutProtectingKing.clear();
        movesWithoutProtectingKing.addAll(unlimitedMovesInGivenDirections(game, moveDirections));
    }

    @Override
    public String toString() {
        return "R";
    }
}
