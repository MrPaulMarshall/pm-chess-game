package chessgame.model.moves;

import chessgame.model.pieces.Piece;
import chessgame.model.game.Game;
import chessgame.model.properties.Position;

/**
 * @author Paweł Marszał
 *
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
     * Executes move
     * @param game provides context
     */
    public abstract void execute(Game game);

    /**
     * Undoes execution of move
     * @param game provides context
     */
    public abstract void undo(Game game);

    /**
     * Used to print move on the logger
     * @return representation of the move in 'chess notation'
     */
    public abstract String toString();

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

}
