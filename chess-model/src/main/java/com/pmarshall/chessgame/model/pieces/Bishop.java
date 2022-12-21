package com.pmarshall.chessgame.model.pieces;

import com.pmarshall.chessgame.model.properties.PlayerColor;
import com.pmarshall.chessgame.model.game.Game;

/**
 * @author Paweł Marszał
 *
 * Extends abstract class Piece
 */
public class Bishop extends Piece {
    // directions: left-up, right-up, right-down, left-down
    static private final int[][] moveDirections = {
            {-1, 1},
            {1, 1},
            {1, -1},
            {-1, -1}
    };

    public Bishop(PlayerColor playerColor) {
        super(playerColor);
    }

    @Override
    public void updateMovesWithoutProtectingKing(Game game) {
        movesWithoutProtectingKing.clear();
        movesWithoutProtectingKing.addAll(unlimitedMovesInGivenDirections(game, moveDirections));
    }

    @Override
    public String toString() {
        return "B";
    }
}
