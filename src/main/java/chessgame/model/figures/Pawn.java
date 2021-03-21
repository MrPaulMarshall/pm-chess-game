package chessgame.model.figures;

import chessgame.model.game.Game;
import chessgame.model.game.moves.BasicMove;
import chessgame.model.game.moves.DoublePawnStart;
import chessgame.model.game.moves.Promotion;
import chessgame.model.properties.PlayerColor;
import chessgame.model.properties.Position;

public class Pawn extends Figure {
    // constant dependent on color, which determines vertical direction of the pawn
    private final int s;

    public Pawn(PlayerColor playerColor) {
        super(loadImage(playerColor, "pawn"), playerColor);
        this.s = playerColor == PlayerColor.WHITE ? -1 : 1;
    }

    @Override
    public void updateMovesWithoutProtectingKing(Game game) {
        movesWithoutProtectingKing.clear();

        // 2 fields forward - if first move, so new position is valid
        if (didNotMoveYet) {
            if (game.board[position.x][position.y + s] == null &&
                    game.board[position.x][position.y + 2*s] == null) {
                movesWithoutProtectingKing.add(new DoublePawnStart(
                        this, new Position(position.x, position.y + 2*s)));
            }
        }

        // 1 field forward
        if (validPosition(position.x, position.y + s) &&
                game.board[position.x][position.y + s] == null) {

            BasicMove basicMove = new BasicMove(
                    this, new Position(position.x, position.y + s), null, null);

            if (position.y + s == 0 || position.y + s == 7) {
                movesWithoutProtectingKing.add(new Promotion(basicMove));
            }
            else {
                movesWithoutProtectingKing.add(basicMove);
            }
        }



        // left, right
        int[] directions = {-1, 1};

        // capturing diagonally
        for (int i : directions) {
            int x = position.x + i;
            int y = position.y + s;

            if (validPosition(x, y) &&
                    game.board[x][y] != null &&
                    game.board[x][y].playerColor != this.playerColor) {

                BasicMove basicMove = new BasicMove(this, new Position(x, y), game.board[x][y], new Position(x, y));

                if (y == 0 || y == 7) {
                    movesWithoutProtectingKing.add(new Promotion(basicMove));
                }
                else {
                    movesWithoutProtectingKing.add(basicMove);
                }
            }
        }

        // TODO: EnPassant
        //  game needs to remember old moves, or at least 1
        if (game.getLastMove() instanceof DoublePawnStart
                && game.getLastMove().getNewPosition().y == this.position.y
                && Math.abs(game.getLastMove().getNewPosition().x - this.position.x) == 1) {
            // If last move was double-pawn-start by enemy pawn, and they are currently at the same row (and neighbours)

            int x = game.getLastMove().getNewPosition().x;
            int y = position.y + s;

            if (validPosition(x, y) && game.board[x][y] == null) {
                movesWithoutProtectingKing.add(new BasicMove(
                        this, new Position(x, y), game.board[x][position.y], new Position(x, position.y)));
            }
        }
    }
}
