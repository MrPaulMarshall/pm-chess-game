package chessgame.model.figures;

import chessgame.model.game.Chessboard;
import chessgame.model.properties.Color;
import chessgame.model.properties.Position;

public class Pawn extends Figure {
    // constant dependent on color, which determines vertical direction of the pawn
    private final int s;

    public Pawn(String imgSource, Color color) {
        super(imgSource, color);
        this.s = color == Color.WHITE ? 1 : -1;
    }

    @Override
    public void updateMovesWithoutProtectingKing(Chessboard chessboard) {
        movesWithoutProtectingKing.clear();

        // 1 field forward
        if (validPosition(position.x, position.y + s) &&
                chessboard.board[position.x][position.y + s] == null) {
            movesWithoutProtectingKing.add(new Position(position.x, position.y + s));
        }

        // 2 fields forward - if first move, so position is valid
        if (didNotMoveYet) {
            if (chessboard.board[position.x][position.y + s] == null &&
                    chessboard.board[position.x][position.y + 2*s] == null) {
                movesWithoutProtectingKing.add(new Position(position.x, position.y + 2*s));
            }
        }

        // left, right
        int[] directions = {-1, 1};

        // capturing diagonally
        for (int i : directions) {
            int x = position.x + i;
            int y = position.y + s;

            if (validPosition(x, y) &&
                    chessboard.board[x][y] != null &&
                    chessboard.board[x][y].color != this.color) {
                movesWithoutProtectingKing.add(new Position(x, y));
            }
        }
    }
}
