package chessgame.model.game.moves;

import chessgame.model.figures.King;
import chessgame.model.figures.Rook;
import chessgame.model.game.Game;
import chessgame.model.properties.Position;

public class Castling extends Move {

    private Rook rookToMove;
    private Position oldPositionOfRook;
    private Position newPositionForRook;

    public Castling(King king, Position newPositionForKing, Rook rookToMove, Position newPositionForRook) {
        this.movedFigure = king;
        this.oldPosition = king.getPosition().copy();
        this.newPosition = newPositionForKing;

        this.rookToMove = rookToMove;
        this.oldPositionOfRook = rookToMove.getPosition().copy();
        this.newPositionForRook = newPositionForRook;
    }

    @Override
    public void execute(Game game) {
        game.board[newPosition.x][newPosition.y] = this.movedFigure;
        game.board[oldPosition.x][oldPosition.y] = null;
        this.movedFigure.setPosition(newPosition);
        this.movedFigure.markThatFigureMoved();

        game.board[newPositionForRook.x][newPositionForRook.y] = this.rookToMove;
        game.board[oldPositionOfRook.x][oldPositionOfRook.y] = null;
        this.rookToMove.setPosition(newPositionForRook);
        this.rookToMove.markThatFigureMoved();
    }

    @Override
    public void undo(Game game) {
        this.movedFigure.undoMarkThatFigureMoved();
        game.board[newPosition.x][newPosition.y] = null;
        game.board[oldPosition.x][oldPosition.y] = this.movedFigure;

        this.rookToMove.undoMarkThatFigureMoved();
        game.board[newPositionForRook.x][newPositionForRook.y] = null;
        game.board[oldPositionOfRook.x][oldPosition.y] = this.rookToMove;
    }

    @Override
    public boolean equals(Object other) {
        return false;
    }
}
