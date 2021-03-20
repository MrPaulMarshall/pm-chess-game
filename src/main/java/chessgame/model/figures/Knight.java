package chessgame.model.figures;

import chessgame.model.game.ChessBoard;
import chessgame.model.properties.PlayerColor;

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

    public Knight(PlayerColor playerColor) {
        super(loadImage(playerColor, "knight"), playerColor);
    }

    @Override
    public void updateMovesWithoutProtectingKing(ChessBoard chessboard) {
        movesWithoutProtectingKing.clear();
        movesWithoutProtectingKing.addAll(movesViaGivenJumps(chessboard, jumps));
    }
}
