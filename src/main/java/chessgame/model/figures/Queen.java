package chessgame.model.figures;

import chessgame.model.game.Chessboard;
import chessgame.model.properties.Color;

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

    public Queen(String imgSource, Color color) {
        super(imgSource, color);
    }

    @Override
    public void updateMovesWithoutProtectingKing(Chessboard chessboard) {
        movesWithoutProtectingKing.clear();
        movesWithoutProtectingKing.addAll(unlimitedMovesInGivenDirections(chessboard, moveDirections));
    }
}
