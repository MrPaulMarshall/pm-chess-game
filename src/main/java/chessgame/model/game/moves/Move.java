package chessgame.model.game.moves;

import chessgame.model.pieces.Piece;
import chessgame.model.game.Game;
import chessgame.model.properties.Position;

/**
 * Interface representing executable moves on the chessboard.<br>
 * Possible actions are {occupy other location, capture enemy piece, promote a pawn}, and so on
 */
public abstract class Move {
    protected Piece movedPiece;
    protected Position oldPosition;
    protected Position newPosition;

    protected Piece takenPiece;
    protected Position takenPiecePosition;

    protected boolean pieceDidNotMoveBefore;

    public abstract void execute(Game game);

    public abstract void undo(Game game);

    public abstract String toString();

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
