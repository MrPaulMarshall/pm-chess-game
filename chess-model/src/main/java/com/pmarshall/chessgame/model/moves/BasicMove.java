package com.pmarshall.chessgame.model.moves;

import com.pmarshall.chessgame.model.dto.DefaultMove;
import com.pmarshall.chessgame.model.dto.EnPassant;
import com.pmarshall.chessgame.model.dto.LegalMove;
import com.pmarshall.chessgame.model.pieces.Pawn;
import com.pmarshall.chessgame.model.pieces.Piece;
import com.pmarshall.chessgame.model.properties.Position;
import com.pmarshall.chessgame.model.game.InMemoryChessGame;

import java.util.List;

/**
 * @author Paweł Marszał
 *
 * Represents basic move on the chessboard: one piece changes position, and potentially takes enemy piece
 */
public class BasicMove extends Move {
    public BasicMove(Piece movedPiece, Position newPosition, Piece takenPiece, Position takenPiecePosition) {
        this.movedPiece = movedPiece;
        this.oldPosition = movedPiece.getPosition();
        this.newPosition = newPosition;

        this.takenPiece = takenPiece;
        this.takenPiecePosition = takenPiecePosition;

        this.pieceDidNotMoveBefore = movedPiece.getDidNotMoveFlag();
    }

    @Override
    public void execute(InMemoryChessGame game) {
        // remove piece, if there is any
        if (this.takenPiece != null) {
            game.removePiece(this.takenPiece);
        }

        // change piece's position
        game.board[oldPosition.x()][oldPosition.y()] = null;
        game.board[newPosition.x()][newPosition.y()] = movedPiece;
        movedPiece.setPosition(newPosition);
        movedPiece.markThatFigureMoved();
    }

    @Override
    public void undo(InMemoryChessGame game) {
        // restore flag
        if (this.pieceDidNotMoveBefore) {
            movedPiece.undoMarkThatFigureMoved();
        }

        // change piece's position
        movedPiece.setPosition(oldPosition);
        game.board[oldPosition.x()][oldPosition.y()] = this.movedPiece;
        game.board[newPosition.x()][newPosition.y()] = null;

        // restore piece, if one was taken
        if (this.takenPiece != null) {
            game.addPieceBack(this.takenPiece, this.takenPiecePosition);
        }
    }

    @Override
    public LegalMove toDto(List<Move> legalMoves) {
        if (movedPiece instanceof Pawn && takenPiece != null && !takenPiecePosition.equals(newPosition)) {
            return new EnPassant(
                    movedPiece.getPosition(), newPosition, takenPiecePosition, withCheck, inNotation(legalMoves));
        }

        return new DefaultMove(movedPiece.getPosition(), newPosition, withCheck, inNotation(legalMoves));
    }

    @Override
    public String inNotation(List<Move> legalMoves) {
        StringBuilder builder = new StringBuilder();

        builder.append(movedPiece.toString());

        differentiateConflictingMoves(builder, legalMoves);

        if (takenPiece != null) {
            builder.append("x");
        }

        builder.append(newPosition.translateX());
        builder.append(newPosition.translateY());

        if (withCheck) {
            builder.append("+");
        }

        return builder.toString();
    }

    /**
     * Appends to builder minimal amount of information about the starting position,
     * that is needed to differentiate between pieces of the same type that can move into the target position.
     */
    private void differentiateConflictingMoves(StringBuilder builder, List<Move> legalMoves) {
        Position from = movedPiece.getPosition();

        if (movedPiece instanceof Pawn) {
            if (takenPiece != null) {
                builder.append(from.translateX());
            }
            return;
        }

        List<Position> conflictingMovesStartingPositions = legalMoves.stream()
                .filter(move -> move.getNewPosition().equals(newPosition))
                .map(Move::getPieceToMove)
                .filter(piece -> piece.getType() == movedPiece.getType())
                .map(Piece::getPosition)
                .filter(position -> !position.equals(from))
                .toList();

        boolean conflictOnRank = conflictingMovesStartingPositions.stream()
                .anyMatch(position -> position.x() != from.x() && position.y() == from.y());
        boolean conflictOnFile = conflictingMovesStartingPositions.stream()
                .anyMatch(position -> position.x() == from.x() && position.y() != from.y());
        boolean conflictElsewhere = conflictingMovesStartingPositions.stream()
                .anyMatch(position -> position.x() != from.x() && position.y() != from.y());

        if (conflictOnFile && conflictOnRank) {
            builder.append(from.translateX()).append(from.translateY());
            return;
        }

        if (conflictOnFile) {
            builder.append(from.translateY());
            return;
        }

        if (conflictOnRank || conflictElsewhere) {
            builder.append(from.translateX());
        }
    }
}
