package chessgame.model.figures;

import chessgame.model.game.Chessboard;
import chessgame.model.properties.Color;

public class Rook extends Figure {
    // directions: up, right, down, left
    static private final int[][] moveDirections = {
            {0, 1},
            {1, 0},
            {0, -1},
            {-1, 0}
    };

    public Rook(String imgSource, Color color) {
        super(imgSource, color);
    }

    @Override
    public void updateMovesWithoutProtectingKing(Chessboard chessboard) {
        movesWithoutProtectingKing.clear();
        movesWithoutProtectingKing.addAll(unlimitedMovesInGivenDirections(chessboard, moveDirections));
    }
}
