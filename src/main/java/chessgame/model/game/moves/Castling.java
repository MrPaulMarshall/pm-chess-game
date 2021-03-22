package chessgame.model.game.moves;

import chessgame.model.pieces.King;
import chessgame.model.pieces.Rook;
import chessgame.model.game.Game;
import chessgame.model.properties.Position;

public class Castling extends Move {

    private final Rook rookToMove;
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
        game.board[newPosition.x][newPosition.y] = this.movedPiece;
        game.board[oldPosition.x][oldPosition.y] = null;
        this.movedPiece.setPosition(newPosition);
        this.movedPiece.markThatFigureMoved();

        game.board[newPositionForRook.x][newPositionForRook.y] = this.rookToMove;
        game.board[oldPositionOfRook.x][oldPositionOfRook.y] = null;
        this.rookToMove.setPosition(newPositionForRook);
        this.rookToMove.markThatFigureMoved();
    }

    @Override
    public void undo(Game game) {
        this.movedPiece.undoMarkThatFigureMoved();
        this.movedPiece.setPosition(oldPosition);
        game.board[newPosition.x][newPosition.y] = null;
        game.board[oldPosition.x][oldPosition.y] = this.movedPiece;

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
