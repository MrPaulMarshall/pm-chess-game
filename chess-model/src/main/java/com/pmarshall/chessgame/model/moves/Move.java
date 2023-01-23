package com.pmarshall.chessgame.model.moves;

import com.pmarshall.chessgame.model.dto.LegalMove;
import com.pmarshall.chessgame.model.game.InMemoryChessGame;
import com.pmarshall.chessgame.model.pieces.Piece;
import com.pmarshall.chessgame.model.properties.Position;

import java.util.List;

/**
 * @author Paweł Marszał
 * <p>
 * Interface representing executable moves on the chessboard.
 * I used Command design pattern to create it.
 */
public abstract class Move {
    /**
     * Piece that is being moved
     */
    protected Piece movedPiece;

    /**
     * Position of piece before it is being moved
     */
    protected Position oldPosition;

    /**
     * Position of piece after it is being moved
     */
    protected Position newPosition;

    /**
     * Enemy piece that is being taken
     */
    protected Piece takenPiece;

    /**
     * Position of taken piece
     */
    protected Position takenPiecePosition;

    /**
     * Flag that stores information if piece is being moved for the first time
     * Needed to restore proper state by #undo()
     */
    protected boolean pieceDidNotMoveBefore;

    /**
     * Flag that tell whether enemy king would be put in check by this move
     */
    protected boolean withCheck;

    /**
     * Executes move
     * @param game provides context
     */
    public abstract void execute(InMemoryChessGame game);

    /**
     * Undoes execution of move
     * @param game provides context
     */
    public abstract void undo(InMemoryChessGame game);

    // Getters

    public Position getNewPosition() {
        return this.newPosition;
    }

    public Piece getPieceToMove() {
        return this.movedPiece;
    }

    public Piece getPieceToTake() {
        return this.takenPiece;
    }

    public boolean isWithCheck() {
        return this.withCheck;
    }

    public void setWithCheck(boolean check) {
        this.withCheck = check;
    }

    public abstract LegalMove toDto(List<Move> legalMove);

    /**
     * @param legalMoves list of all legal moves in current turn.
     *                   Needed to distinguish pieces of the same type that move into the same position
     * @return representation of the move in algebraic notation
     */
    public abstract String inNotation(List<Move> legalMoves);
}
