package com.pmarshall.chessgame.model.pieces;

import com.pmarshall.chessgame.model.properties.Color;
import com.pmarshall.chessgame.model.properties.PieceType;
import com.pmarshall.chessgame.model.properties.Position;
import com.pmarshall.chessgame.model.game.InMemoryChessGame;
import com.pmarshall.chessgame.model.moves.Castling;

/**
 * @author Paweł Marszał
 *
 * Extends abstract class Piece
 */
public final class King extends Piece {
    /**
     * Stores information if king is under check
     */
    private boolean isChecked = false;

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

    public King(Color color) {
        super(color);
    }

    public void setIsChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    public boolean getIsChecked() {
        return this.isChecked;
    }

    @Override
    public void updateMovesWithoutProtectingKing(InMemoryChessGame game) {
        movesWithoutProtectingKing.clear();

        // normal moves in all directions, but only of length 1
        movesWithoutProtectingKing.addAll(movesViaGivenJumps(game, jumps));

        // Castling
        if (didNotMoveYet) {
            /*
            Piece is starting rook (eligible for castling) if:
            1. it has not moved yet
            2. it has the same color as king, so it's not promoted piece of the opponent
             */

            Piece leftRook = game.board[position.rank()][0];
            if (leftRook != null && leftRook.color == color && leftRook.didNotMoveYet) {
                boolean castlingPossible = true;

                // all cells between king and rook must be free
                for (int file = 1; file < position.file(); file++) {
                    if (game.board[position.rank()][file] != null) {
                        castlingPossible = false;
                        break;
                    }
                }

                // king cannot be threatened anywhere on his path
                for (int i = 0; i <= 2 && castlingPossible; i++) {
                    if (game.isPosThreatened(new Position(position.rank(), position.file() - i), game.getOtherPlayer())) {
                        castlingPossible = false;
                    }
                }

                if (castlingPossible) {
                    movesWithoutProtectingKing.add(new Castling(
                            this, new Position(position.rank(), position.file() - 2),
                            (Rook)leftRook, new Position(position.rank(), position.file() - 1)));
                }
            }

            Piece rightRook = game.board[position.rank()][7];
            if (rightRook != null && rightRook.color == color && rightRook.didNotMoveYet) {
                boolean castlingPossible = true;

                // all cells between king and rook must be free
                for (int file = position.file() + 1; file < 7; file++) {
                    if (game.board[position.rank()][file] != null) {
                        castlingPossible = false;
                        break;
                    }
                }

                // king cannot be threatened anywhere on his path
                for (int i = 0; i <= 2 && castlingPossible; i++) {
                    if (game.isPosThreatened(new Position(position.rank(), position.file() + i), game.getOtherPlayer())) {
                        castlingPossible = false;
                    }
                }

                if (castlingPossible) {
                    movesWithoutProtectingKing.add(new Castling(
                            this, new Position(position.rank(), position.file() + 2),
                            (Rook)rightRook, new Position(position.rank(), position.file() + 1)));
                }
            }
        }
    }

    @Override
    public PieceType getType() {
        return PieceType.KING;
    }

    @Override
    public String toString() {
        return "K";
    }
}
