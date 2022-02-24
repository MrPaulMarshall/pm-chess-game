package chessgame.model.moves;

import chessgame.model.pieces.Pawn;
import chessgame.model.pieces.Piece;
import chessgame.model.game.Game;
import chessgame.model.properties.Position;

/**
 * @author Paweł Marszał
 *
 * Represents basic move on the chessboard: one piece changes position, and potentially takes enemy piece
 */
public class BasicMove extends Move {
    public BasicMove(Piece movedPiece, Position newPosition, Piece takenPiece, Position takenPiecePosition) {
        this.movedPiece = movedPiece;
        this.oldPosition = movedPiece.getPosition().copy();
        this.newPosition = newPosition.copy();

        this.takenPiece = takenPiece;
        this.takenPiecePosition = takenPiecePosition;

        this.pieceDidNotMoveBefore = movedPiece.getDidNotMoveFlag();
    }

    @Override
    public void execute(Game game) {
        // remove piece, if there is any
        if (this.takenPiece != null) {
            game.removePiece(this.takenPiece);
        }

        // change piece's position
        game.board[oldPosition.x][oldPosition.y] = null;
        game.board[newPosition.x][newPosition.y] = movedPiece;
        movedPiece.setPosition(newPosition);
        movedPiece.markThatFigureMoved();
    }

    @Override
    public void undo(Game game) {
        // restore flag
        if (this.pieceDidNotMoveBefore) {
            movedPiece.undoMarkThatFigureMoved();
        }

        // change piece's position
        movedPiece.setPosition(oldPosition);
        game.board[oldPosition.x][oldPosition.y] = this.movedPiece;
        game.board[newPosition.x][newPosition.y] = null;

        // restore piece, if one was taken
        if (this.takenPiece != null) {
            game.addPieceBack(this.takenPiece, this.takenPiecePosition);
        }
    }

    @Override
    public String toString() {
        if (this.movedPiece instanceof Pawn) {
            return (this.takenPiece == null ? "" : this.oldPosition.translateX() + "x")
                    + this.newPosition.translateX() + this.newPosition.translateY();
        }

        return this.movedPiece.toString() + (this.takenPiece == null ? "" : "x")
                + this.newPosition.translateX() + this.newPosition.translateY();
    }

}
