package chessgame.model.pieces;

import chessgame.model.game.Game;
import chessgame.model.game.moves.Castling;
import chessgame.model.properties.PlayerColor;
import chessgame.model.properties.Position;

public class King extends Piece {
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

    public King(PlayerColor playerColor) {
        super(loadImage(playerColor, "king"), playerColor);
    }

    public void setIsChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    public boolean getIsChecked() {
        return this.isChecked;
    }

    @Override
    public void updateMovesWithoutProtectingKing(Game game) {
        movesWithoutProtectingKing.clear();

        // normal moves
        movesWithoutProtectingKing.addAll(movesViaGivenJumps(game, jumps));

        // Castling
        if (didNotMoveYet) {
            // If piece in place of rook hasn't moved yet, then it IS unmoved rook

            // LEFT ROOK
            if (game.board[0][this.position.y] != null && game.board[0][this.position.y].didNotMoveYet) {
                boolean castlingPossible = true;

                // all cells between king and rook must be free
                for (int x = 1; x < this.position.x; x++) {
                    if (game.board[x][this.position.y] != null) {
                        castlingPossible = false;
                        break;
                    }
                }

                // king cannot be threaten anywhere on his path
                for (int i = 0; i <= 2 && castlingPossible; i++) {
                    if (game.isPosThreaten(new Position(this.position.x - i, this.position.y), game.getOtherPlayer())) {
                        castlingPossible = false;
                    }
                }

                if (castlingPossible) {
                    movesWithoutProtectingKing.add(new Castling(
                            this, new Position(this.position.x - 2, this.position.y),
                            (Rook)game.board[0][this.position.y], new Position(this.position.x - 1, this.position.y)));
                }
            }

            // RIGHT ROOK
            if (game.board[7][this.position.y] != null && game.board[7][this.position.y].didNotMoveYet) {
                boolean castlingPossible = true;

                // all cells between king and rook must be free
                for (int x = this.position.x + 1; x < 7; x++) {
                    if (game.board[x][this.position.y] != null) {
                        castlingPossible = false;
                        break;
                    }
                }

                // king cannot be threaten anywhere on his path
                for (int i = 0; i <= 2 && castlingPossible; i++) {
                    if (game.isPosThreaten(new Position(this.position.x + i, this.position.y), game.getOtherPlayer())) {
                        castlingPossible = false;
                    }
                }

                if (castlingPossible) {
                    movesWithoutProtectingKing.add(new Castling(
                            this, new Position(this.position.x + 2, this.position.y),
                            (Rook)game.board[7][this.position.y], new Position(this.position.x + 1, this.position.y)));
                }
            }
        }
    }
}
