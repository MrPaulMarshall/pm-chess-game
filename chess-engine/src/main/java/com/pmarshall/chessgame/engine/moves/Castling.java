package com.pmarshall.chessgame.engine.moves;

import com.pmarshall.chessgame.model.dto.LegalMove;
import com.pmarshall.chessgame.engine.pieces.King;
import com.pmarshall.chessgame.engine.pieces.Rook;
import com.pmarshall.chessgame.model.properties.Position;
import com.pmarshall.chessgame.engine.game.InMemoryChessGame;

import java.util.List;

import static java.lang.Math.abs;

/**
 * @author Paweł Marszał
 * <p>
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
        this.oldPosition = king.getPosition();
        this.newPosition = newPositionForKing;

        this.rookToMove = rookToMove;
        this.oldPositionOfRook = rookToMove.getPosition();
        this.newPositionForRook = newPositionForRook;
    }

    @Override
    public void execute(InMemoryChessGame game) {
        // King's jump
        game.board[newPosition.rank()][newPosition.file()] = movedPiece;
        game.board[oldPosition.rank()][oldPosition.file()] = null;
        movedPiece.setPosition(newPosition);
        movedPiece.markThatFigureMoved();

        // Rook's jump
        game.board[newPositionForRook.rank()][newPositionForRook.file()] = rookToMove;
        game.board[oldPositionOfRook.rank()][oldPositionOfRook.file()] = null;
        rookToMove.setPosition(newPositionForRook);
        rookToMove.markThatFigureMoved();
    }

    @Override
    public void undo(InMemoryChessGame game) {
        // undo King's jump
        movedPiece.undoMarkThatFigureMoved();
        movedPiece.setPosition(oldPosition);
        game.board[newPosition.rank()][newPosition.file()] = null;
        game.board[oldPosition.rank()][oldPosition.file()] = movedPiece;

        // undo Rook's jump
        rookToMove.undoMarkThatFigureMoved();
        rookToMove.setPosition(oldPositionOfRook);
        game.board[newPositionForRook.rank()][newPositionForRook.file()] = null;
        game.board[oldPositionOfRook.rank()][oldPositionOfRook.file()] = rookToMove;
    }

    @Override
    public LegalMove toDto(List<Move> legalMoves) {
        int distance = abs(newPositionForRook.file() - oldPositionOfRook.file());
        return new com.pmarshall.chessgame.model.dto.Castling(
                movedPiece.getPosition(), newPosition, distance == 3, moveEffect, inNotation(legalMoves));
    }

    @Override
    public String inNotation(List<Move> legalMoves) {
        int distance = abs(newPositionForRook.file() - oldPositionOfRook.file());
        return (distance == 2 ? "0-0" : "0-0-0") + getMoveEffectCode();
    }
}
