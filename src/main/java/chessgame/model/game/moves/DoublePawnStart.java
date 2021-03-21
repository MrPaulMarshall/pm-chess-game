package chessgame.model.game.moves;

import chessgame.model.figures.Pawn;
import chessgame.model.game.Game;
import chessgame.model.properties.Position;

public class DoublePawnStart extends Move {

    public DoublePawnStart(Pawn movingPawn, Position newPosition) {
        this.movedFigure = movingPawn;
        this.oldPosition = movingPawn.getPosition().copy();
        this.newPosition = newPosition.copy();

        this.takenPiece = null;
        this.takenPiecePosition = null;

        this.wasFigureMovedBefore = movingPawn.getMovedFlag();
    }

    @Override
    public void execute(Game game) {
        game.board[oldPosition.x][oldPosition.y] = null;
        game.board[newPosition.x][newPosition.y] = movedFigure;
        this.movedFigure.setPosition(newPosition);

        this.movedFigure.markThatFigureMoved();
    }

    @Override
    public void undo(Game game) {
        if (this.wasFigureMovedBefore) {
            this.movedFigure.undoMarkThatFigureMoved();
        }

        this.movedFigure.setPosition(oldPosition);
        game.board[oldPosition.x][oldPosition.y] = movedFigure;
        game.board[newPosition.x][newPosition.y] = null;
    }

    @Override
    public boolean equals(Object other) {
        return false;
    }
}
