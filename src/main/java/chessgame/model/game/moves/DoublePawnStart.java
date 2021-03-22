package chessgame.model.game.moves;

import chessgame.model.pieces.Pawn;
import chessgame.model.game.Game;
import chessgame.model.properties.Position;

public class DoublePawnStart extends Move {

    public DoublePawnStart(Pawn movingPawn, Position newPosition) {
        this.movedPiece = movingPawn;
        this.oldPosition = movingPawn.getPosition().copy();
        this.newPosition = newPosition.copy();

        this.takenPiece = null;
        this.takenPiecePosition = null;

        this.pieceDidNotMoveBefore = movingPawn.getDidNotMoveFlag();
    }

    @Override
    public void execute(Game game) {
        game.board[oldPosition.x][oldPosition.y] = null;
        game.board[newPosition.x][newPosition.y] = movedPiece;
        this.movedPiece.setPosition(newPosition);

        this.movedPiece.markThatFigureMoved();
    }

    @Override
    public void undo(Game game) {
        if (this.pieceDidNotMoveBefore) {
            this.movedPiece.undoMarkThatFigureMoved();
        }

        this.movedPiece.setPosition(oldPosition);
        game.board[oldPosition.x][oldPosition.y] = movedPiece;
        game.board[newPosition.x][newPosition.y] = null;
    }

    @Override
    public String toString() {
        return this.newPosition.translateX() + this.newPosition.translateY();
    }

}
