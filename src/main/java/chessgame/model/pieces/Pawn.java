package chessgame.model.pieces;

import chessgame.model.game.Game;
import chessgame.model.moves.BasicMove;
import chessgame.model.moves.DoublePawnStart;
import chessgame.model.moves.Promotion;
import chessgame.model.properties.PlayerColor;
import chessgame.model.properties.Position;

/**
 * @author Paweł Marszał
 *
 * Extends abstract class Piece
 */
public class Pawn extends Piece {
    /**
     * Constant that determines direction of pawn's move
     *  whites: -1 (bottom-up)
     *  blacks: 1  (top-down)
     */
    private final int s;

    public Pawn(PlayerColor playerColor) {
        super(loadImage(playerColor, "pawn"), playerColor);
        this.s = playerColor == PlayerColor.WHITE ? -1 : 1;
    }

    @Override
    public void updateMovesWithoutProtectingKing(Game game) {
        movesWithoutProtectingKing.clear();

        // 2 fields forward - as it is pawn's first move, positions ahead are certainly valid
        if (didNotMoveYet) {
            if (game.board[position.x][position.y + s] == null &&
                    game.board[position.x][position.y + 2*s] == null) {

                movesWithoutProtectingKing.add(new DoublePawnStart(
                        this, new Position(position.x, position.y + 2*s)));
            }
        }

        // 1 field forward - normal straight pawn's move
        if (validPosition(position.x, position.y + s) &&
                game.board[position.x][position.y + s] == null) {

            BasicMove basicMove = new BasicMove(
                    this, new Position(position.x, position.y + s), null, null);

            if (position.y + s == 0 || position.y + s == 7) {
                // if piece lands on the last row, promotion must be executed afterwards
                movesWithoutProtectingKing.add(new Promotion(basicMove));
            }
            else {
                // if piece is in the center, just move pawn
                movesWithoutProtectingKing.add(basicMove);
            }
        }



        // left, right - diagonal direction for capturing enemy pieces
        int[] directions = {-1, 1};

        // normal capturing diagonally
        for (int i : directions) {
            int x = position.x + i;
            int y = position.y + s;

            if (validPosition(x, y) &&
                    game.board[x][y] != null &&
                    game.board[x][y].playerColor != this.playerColor) {

                BasicMove basicMove = new BasicMove(this, new Position(x, y), game.board[x][y], new Position(x, y));

                if (y == 0 || y == 7) {
                    // if piece lands on the last row, promotion must be executed afterwards
                    movesWithoutProtectingKing.add(new Promotion(basicMove));
                }
                else {
                    movesWithoutProtectingKing.add(basicMove);
                }
            }
        }

        // Capturing EnPassant
        //  if last move was double-pawn-start by enemy pawn, and they are currently at the same row (and neighbours)
        //  as double-pawn-start was executed, the target-field is certainly free
        if (game.getLastMove() instanceof DoublePawnStart
                && game.getLastMove().getNewPosition().y == this.position.y
                && Math.abs(game.getLastMove().getNewPosition().x - this.position.x) == 1) {

            int x = game.getLastMove().getNewPosition().x;
            int y = position.y + s;

            if (validPosition(x, y) && game.board[x][y] == null) {
                movesWithoutProtectingKing.add(new BasicMove(
                        this, new Position(x, y), game.board[x][position.y], new Position(x, position.y)));
            }
        }
    }

    @Override
    public String toString() {
        return this.position.translateX();
    }
}
