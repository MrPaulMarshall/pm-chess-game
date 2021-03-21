package chessgame.model.game.moves;

import chessgame.model.figures.Figure;
import chessgame.model.game.Game;
import chessgame.model.properties.Position;

public class BasicMove extends Move {
    public BasicMove(Figure movedFigure, Position newPosition, Figure takenPiece, Position takenPiecePosition) {
        this.movedFigure = movedFigure;
        this.oldPosition = movedFigure.getPosition().copy();
        this.newPosition = newPosition.copy();

        this.takenPiece = takenPiece;
        this.takenPiecePosition = takenPiecePosition;

        this.wasFigureMovedBefore = movedFigure.getMovedFlag();
    }

    @Override
    public void execute(Game game) {
        // remove piece, if there is any
        if (this.takenPiece != null) {
            game.removePiece(this.takenPiece);
        }

        game.board[oldPosition.x][oldPosition.y] = null;
        game.board[newPosition.x][newPosition.y] = movedFigure;
        movedFigure.setPosition(newPosition);

        movedFigure.markThatFigureMoved();
    }

    @Override
    public void undo(Game game) {
        if (!this.wasFigureMovedBefore) {
            movedFigure.undoMarkThatFigureMoved();
        }

        movedFigure.setPosition(oldPosition);
        game.board[oldPosition.x][oldPosition.y] = this.movedFigure;
        game.board[newPosition.x][newPosition.y] = null;

        if (this.takenPiece != null) {
            game.addPieceBack(this.takenPiece, this.takenPiecePosition);
        }
    }

    @Override
    public boolean equals(Object other) {
        return false;
    }
}
