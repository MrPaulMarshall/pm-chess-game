package chessgame.model.figures;

import chessgame.model.game.Chessboard;
import chessgame.model.properties.Color;

public class Knight extends Figure {
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

    public Knight(String imgSource, Color color) {
        super(imgSource, color);
    }

    @Override
    public void updateMovesWithoutProtectingKing(Chessboard chessboard) {
        movesWithoutProtectingKing.clear();
        movesWithoutProtectingKing.addAll(movesViaGivenJumps(chessboard, jumps));
    }
}
