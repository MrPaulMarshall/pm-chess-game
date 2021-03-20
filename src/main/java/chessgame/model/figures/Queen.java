package chessgame.model.figures;

import chessgame.model.game.ChessBoard;
import chessgame.model.properties.PlayerColor;

public class Queen extends Figure {
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

    public Queen(String imgSource, PlayerColor playerColor) {
        super(loadImage(playerColor, "queen"), playerColor);
    }

    @Override
    public void updateMovesWithoutProtectingKing(ChessBoard chessboard) {
        movesWithoutProtectingKing.clear();
        movesWithoutProtectingKing.addAll(unlimitedMovesInGivenDirections(chessboard, moveDirections));
    }
}
