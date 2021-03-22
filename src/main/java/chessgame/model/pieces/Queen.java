package chessgame.model.pieces;

import chessgame.model.game.Game;
import chessgame.model.properties.PlayerColor;

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

    public Queen(PlayerColor playerColor) {
        super(loadImage(playerColor, "queen"), playerColor);
    }

    @Override
    public void updateMovesWithoutProtectingKing(Game game) {
        movesWithoutProtectingKing.clear();
        movesWithoutProtectingKing.addAll(unlimitedMovesInGivenDirections(game, moveDirections));
    }
}
