package chessgame.model.game.moves;

import chessgame.model.figures.Figure;
import chessgame.model.game.Game;
import chessgame.model.properties.Position;

/**
 * Interface representing executable moves on the chessboard.<br>
 * Possible actions are {occupy other location, capture enemy piece, promote a pawn}, and so on
 */
public abstract class Move {
    protected Figure movedFigure;
    protected Position oldPosition;
    protected Position newPosition;

    protected Figure takenPiece;
    protected Position takenPiecePosition;

    protected boolean wasFigureMovedBefore;

    public abstract void execute(Game game);

    public abstract void undo(Game game);

    public abstract boolean equals(Object other);

    public Position getNewPosition() {
        return this.newPosition;
    }

    public Figure getPieceToMove() {
        return this.movedFigure;
    }

    public Figure getPieceToTake() {
        return this.takenPiece;
    }
}
