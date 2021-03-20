package chessgame.model.figures;

import chessgame.model.game.ChessBoard;
import chessgame.model.properties.PlayerColor;

public class Rook extends Figure {
    // directions: up, right, down, left
    static private final int[][] moveDirections = {
            {0, 1},
            {1, 0},
            {0, -1},
            {-1, 0}
    };

    public Rook(PlayerColor playerColor) {
        super(loadImage(playerColor, "rook"), playerColor);
    }

    @Override
    public void updateMovesWithoutProtectingKing(ChessBoard chessboard) {
        movesWithoutProtectingKing.clear();
        movesWithoutProtectingKing.addAll(unlimitedMovesInGivenDirections(chessboard, moveDirections));
    }
}
