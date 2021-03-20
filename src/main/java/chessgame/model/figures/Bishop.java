package chessgame.model.figures;

import chessgame.model.game.ChessBoard;
import chessgame.model.properties.PlayerColor;

public class Bishop extends Figure {
    // directions: left-up, right-up, right-down, left-down
    static private final int[][] moveDirections = {
            {-1, 1},
            {1, 1},
            {1, -1},
            {-1, -1}
    };

    public Bishop(PlayerColor playerColor) {
        super(loadImage(playerColor, "bishop"), playerColor);
    }

    @Override
    public void updateMovesWithoutProtectingKing(ChessBoard chessboard) {
        movesWithoutProtectingKing.clear();
        movesWithoutProtectingKing.addAll(unlimitedMovesInGivenDirections(chessboard, moveDirections));
    }
}
