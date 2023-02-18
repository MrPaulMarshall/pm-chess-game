package com.pmarshall.chessgame.engine.moves;

import com.pmarshall.chessgame.model.dto.DefaultMove;
import com.pmarshall.chessgame.model.dto.LegalMove;
import com.pmarshall.chessgame.engine.game.InMemoryChessGame;
import com.pmarshall.chessgame.engine.pieces.Pawn;
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
        game.board[oldPosition.rank()][oldPosition.file()] = null;
        game.board[newPosition.rank()][newPosition.file()] = movedPiece;
        movedPiece.setPosition(newPosition);

        movedPiece.markThatFigureMoved();
    }

    @Override
    public void undo(InMemoryChessGame game) {
        if (pieceDidNotMoveBefore) {
            movedPiece.undoMarkThatFigureMoved();
        }

        movedPiece.setPosition(oldPosition);
        game.board[oldPosition.rank()][oldPosition.file()] = movedPiece;
        game.board[newPosition.rank()][newPosition.file()] = null;
    }

    @Override
    public LegalMove toDto(List<Move> legalMoves) {
        return new DefaultMove(movedPiece.getPosition(), newPosition, moveEffect, inNotation(legalMoves));
    }

    @Override
    public String inNotation(List<Move> legalMoves) {
        return newPosition.strFile() + newPosition.strRank();
    }

}
