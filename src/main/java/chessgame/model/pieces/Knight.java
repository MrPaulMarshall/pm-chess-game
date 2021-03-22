package chessgame.model.pieces;

import chessgame.model.game.Game;
import chessgame.model.properties.PlayerColor;

public class Knight extends Piece {
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

    public Knight(PlayerColor playerColor) {
        super(loadImage(playerColor, "knight"), playerColor);
    }

    @Override
    public void updateMovesWithoutProtectingKing(Game game) {
        movesWithoutProtectingKing.clear();
        movesWithoutProtectingKing.addAll(movesViaGivenJumps(game, jumps));
    }

    @Override
    public String toString() {
        return "N";
    }
}
