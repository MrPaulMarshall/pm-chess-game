package chessgame.model.figures;

import chessgame.model.game.Chessboard;
import chessgame.model.properties.Color;

public class King extends Figure {
    // 1-cell steps in all directions, starting from up-left and going clockwise
    static private final int[][] jumps = {
            {-1, 1},
            {0, 1},
            {1, 1},
            {1, 0},
            {1, -1},
            {0, -1},
            {-1, -1},
            {-1, 0}
    };

    private boolean isChecked = false;

    public King(String imgSource, Color color) {
        super(imgSource, color);
    }

    public void setIsChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    @Override
    public void updateMovesWithoutProtectingKing(Chessboard chessboard) {
        movesWithoutProtectingKing.clear();

        // roszady
        if (didNotMoveYet && !isChecked) {
            // TODO

            // find rooks
            //  Figure[] = {left_rook, right_rook}

            // for each rook check...
            // ... if it hasn't moved yet
            // ... if there aren't any figures between them
            // ... if none of the cells that King passes are threaten - THAT WILL BE HARD
            // ... if they are in the same (starting) row: king.y == rook.y
        }

        movesWithoutProtectingKing.addAll(movesViaGivenJumps(chessboard, jumps));
    }
}
