package com.pmarshall.chessgame.model.moves;

import com.pmarshall.chessgame.model.dto.DefaultMove;
import com.pmarshall.chessgame.model.dto.LegalMove;
import com.pmarshall.chessgame.model.game.InMemoryChessGame;
import com.pmarshall.chessgame.model.pieces.Pawn;
import com.pmarshall.chessgame.model.properties.Position;

import java.util.List;

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
        game.board[oldPosition.file()][oldPosition.rank()] = null;
        game.board[newPosition.file()][newPosition.rank()] = movedPiece;
        this.movedPiece.setPosition(newPosition);

        this.movedPiece.markThatFigureMoved();
    }

    @Override
    public void undo(InMemoryChessGame game) {
        if (this.pieceDidNotMoveBefore) {
            this.movedPiece.undoMarkThatFigureMoved();
        }

        this.movedPiece.setPosition(oldPosition);
        game.board[oldPosition.file()][oldPosition.rank()] = movedPiece;
        game.board[newPosition.file()][newPosition.rank()] = null;
    }

    @Override
    public LegalMove toDto(List<Move> legalMoves) {
        return new DefaultMove(movedPiece.getPosition(), newPosition, withCheck, inNotation(legalMoves));
    }

    @Override
    public String inNotation(List<Move> legalMoves) {
        return this.newPosition.strFile() + this.newPosition.strRank();
    }

}
