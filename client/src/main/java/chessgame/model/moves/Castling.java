package chessgame.model.moves;

import chessgame.model.pieces.King;
import chessgame.model.pieces.Rook;
import chessgame.model.game.Game;
import chessgame.model.properties.Position;

/**
 * @author Paweł Marszał
 *
 * Represents castling - if King and Rook haven't moved yet and all places in king's path are safe,
 * they can jump behind each other
 */
public class Castling extends Move {

    /**
     * Rook that King castles with
     */
    private final Rook rookToMove;
    /**
     * Old and new positions of that Rook
     */
    private final Position oldPositionOfRook;
    private final Position newPositionForRook;

    public Castling(King king, Position newPositionForKing, Rook rookToMove, Position newPositionForRook) {
        this.movedPiece = king;
        this.oldPosition = king.getPosition().copy();
        this.newPosition = newPositionForKing;

        this.rookToMove = rookToMove;
        this.oldPositionOfRook = rookToMove.getPosition().copy();
        this.newPositionForRook = newPositionForRook;
    }

    @Override
    public void execute(Game game) {
        // King's jump
        game.board[newPosition.x][newPosition.y] = this.movedPiece;
        game.board[oldPosition.x][oldPosition.y] = null;
        this.movedPiece.setPosition(newPosition);
        this.movedPiece.markThatFigureMoved();

        // Rook's jump
        game.board[newPositionForRook.x][newPositionForRook.y] = this.rookToMove;
        game.board[oldPositionOfRook.x][oldPositionOfRook.y] = null;
        this.rookToMove.setPosition(newPositionForRook);
        this.rookToMove.markThatFigureMoved();
    }

    @Override
    public void undo(Game game) {
        // undo King's jump
        this.movedPiece.undoMarkThatFigureMoved();
        this.movedPiece.setPosition(oldPosition);
        game.board[newPosition.x][newPosition.y] = null;
        game.board[oldPosition.x][oldPosition.y] = this.movedPiece;

        // undo Rook's jump
        this.rookToMove.undoMarkThatFigureMoved();
        this.rookToMove.setPosition(oldPositionOfRook);
        game.board[newPositionForRook.x][newPositionForRook.y] = null;
        game.board[oldPositionOfRook.x][oldPositionOfRook.y] = this.rookToMove;
    }

    @Override
    public String toString() {
        int distance = Math.abs(this.oldPosition.y - this.oldPositionOfRook.y);
        return distance == 2 ? "0-0" : "0-0-0";
    }

}
