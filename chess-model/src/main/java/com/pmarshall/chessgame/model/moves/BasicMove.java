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
        if (takenPiece != null) {
            game.removePiece(takenPiece);
        }

        // change piece's position
        game.board[oldPosition.rank()][oldPosition.file()] = null;
        game.board[newPosition.rank()][newPosition.file()] = movedPiece;
        movedPiece.setPosition(newPosition);
        movedPiece.markThatFigureMoved();
    }

    @Override
    public void undo(InMemoryChessGame game) {
        // restore flag
        if (pieceDidNotMoveBefore) {
            movedPiece.undoMarkThatFigureMoved();
        }

        // change piece's position
        movedPiece.setPosition(oldPosition);
        game.board[oldPosition.rank()][oldPosition.file()] = movedPiece;
        game.board[newPosition.rank()][newPosition.file()] = null;

        // restore piece, if one was taken
        if (takenPiece != null) {
            game.addPieceBack(takenPiece, takenPiecePosition);
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

        builder.append(newPosition.strFile());
        builder.append(newPosition.strRank());

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
                builder.append(from.strFile());
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

        // TODO: probably conflictOnRank and conflictOnFile do not require != conditions
        boolean conflictOnRank = conflictingMovesStartingPositions.stream()
                .anyMatch(position -> position.rank() == from.rank() && position.file() != from.file());
        boolean conflictOnFile = conflictingMovesStartingPositions.stream()
                .anyMatch(position -> position.rank() != from.rank() && position.file() == from.file());
        boolean conflictElsewhere = conflictingMovesStartingPositions.stream()
                .anyMatch(position -> position.rank() != from.rank() && position.file() != from.file());

        if (conflictOnFile && conflictOnRank) {
            builder.append(from.strFile()).append(from.strRank());
            return;
        }

        if (conflictOnFile) {
            builder.append(from.strRank());
            return;
        }

        if (conflictOnRank || conflictElsewhere) {
            builder.append(from.strFile());
        }
    }
}
