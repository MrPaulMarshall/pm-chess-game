package com.pmarshall.chessgame.model.pieces;

import com.pmarshall.chessgame.model.properties.Color;
import com.pmarshall.chessgame.model.properties.PieceType;
import com.pmarshall.chessgame.model.properties.Position;
import com.pmarshall.chessgame.model.game.InMemoryChessGame;
import com.pmarshall.chessgame.model.moves.BasicMove;
import com.pmarshall.chessgame.model.moves.DoublePawnStart;
import com.pmarshall.chessgame.model.moves.Promotion;

/**
 * @author Paweł Marszał
 *
 * Extends abstract class Piece
 */
public final class Pawn extends Piece {
    /**
     * Constant that determines direction of pawn's move
     *  whites: -1 (bottom-up)
     *  blacks: 1  (top-down)
     */
    private final int s;

    public Pawn(Color color) {
        super(color);
        this.s = color == Color.WHITE ? -1 : 1;
    }

    @Override
    public void updateMovesWithoutProtectingKing(InMemoryChessGame game) {
        movesWithoutProtectingKing.clear();

        // 2 fields forward - as it is pawn's first move, positions ahead are certainly valid
        if (didNotMoveYet) {
            if (game.board[position.file()][position.rank() + s] == null &&
                    game.board[position.file()][position.rank() + 2*s] == null) {

                movesWithoutProtectingKing.add(new DoublePawnStart(
                        this, new Position(position.file(), position.rank() + 2*s)));
            }
        }

        // 1 field forward - normal straight pawn's move
        if (validPosition(position.file(), position.rank() + s) &&
                game.board[position.file()][position.rank() + s] == null) {

            BasicMove basicMove = new BasicMove(
                    this, new Position(position.file(), position.rank() + s), null, null);

            if (position.rank() + s == 0 || position.rank() + s == 7) {
                // if piece lands on the last row, promotion must be executed afterwards
                movesWithoutProtectingKing.add(new Promotion(basicMove, PieceType.QUEEN));
                movesWithoutProtectingKing.add(new Promotion(basicMove, PieceType.ROOK));
                movesWithoutProtectingKing.add(new Promotion(basicMove, PieceType.BISHOP));
                movesWithoutProtectingKing.add(new Promotion(basicMove, PieceType.KNIGHT));
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
            int x = position.file() + i;
            int y = position.rank() + s;

            if (validPosition(x, y) &&
                    game.board[x][y] != null &&
                    game.board[x][y].color != this.color) {

                BasicMove basicMove = new BasicMove(this, new Position(x, y), game.board[x][y], new Position(x, y));

                if (y == 0 || y == 7) {
                    // if piece lands on the last row, promotion must be executed afterwards
                    movesWithoutProtectingKing.add(new Promotion(basicMove, PieceType.QUEEN));
                    movesWithoutProtectingKing.add(new Promotion(basicMove, PieceType.ROOK));
                    movesWithoutProtectingKing.add(new Promotion(basicMove, PieceType.BISHOP));
                    movesWithoutProtectingKing.add(new Promotion(basicMove, PieceType.KNIGHT));
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
                && game.getLastMove().getNewPosition().rank() == this.position.rank()
                && Math.abs(game.getLastMove().getNewPosition().file() - this.position.file()) == 1) {

            int x = game.getLastMove().getNewPosition().file();
            int y = position.rank() + s;

            if (validPosition(x, y) && game.board[x][y] == null) {
                movesWithoutProtectingKing.add(new BasicMove(
                        this, new Position(x, y), game.board[x][position.rank()], new Position(x, position.rank())));
            }
        }
    }

    @Override
    public PieceType getType() {
        return PieceType.PAWN;
    }

    @Override
    public String toString() {
        return "";
    }
}
