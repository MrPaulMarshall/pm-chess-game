package com.pmarshall.chessgame.model.moves;

import com.pmarshall.chessgame.model.game.InMemoryChessGame;
import com.pmarshall.chessgame.model.pieces.Pawn;
import com.pmarshall.chessgame.model.properties.Position;

/**
 * @author Paweł Marszał
 *
 * Represents double starting move, that pawn can do, if it hasn't moved yet and path is free
 */
public class DoublePawnStart extends Move {

    public DoublePawnStart(Pawn movingPawn, Position newPosition) {
        this.movedPiece = movingPawn;
        this.oldPosition = movingPawn.getPosition();
        this.newPosition = newPosition;

        this.takenPiece = null;
        this.takenPiecePosition = null;

        this.pieceDidNotMoveBefore = movingPawn.getDidNotMoveFlag();
    }

    @Override
    public void execute(InMemoryChessGame game) {
        game.board[oldPosition.x()][oldPosition.y()] = null;
        game.board[newPosition.x()][newPosition.y()] = movedPiece;
        this.movedPiece.setPosition(newPosition);

        this.movedPiece.markThatFigureMoved();
    }

    @Override
    public void undo(InMemoryChessGame game) {
        if (this.pieceDidNotMoveBefore) {
            this.movedPiece.undoMarkThatFigureMoved();
        }

        this.movedPiece.setPosition(oldPosition);
        game.board[oldPosition.x()][oldPosition.y()] = movedPiece;
        game.board[newPosition.x()][newPosition.y()] = null;
    }

    @Override
    public String toString() {
        return this.newPosition.translateX() + this.newPosition.translateY();
    }

}
