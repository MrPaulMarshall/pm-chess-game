package com.pmarshall.chessgame.model.moves;

import com.pmarshall.chessgame.model.dto.LegalMove;
import com.pmarshall.chessgame.model.pieces.*;
import com.pmarshall.chessgame.model.game.InMemoryChessGame;
import com.pmarshall.chessgame.model.properties.Color;
import com.pmarshall.chessgame.model.properties.PieceType;

import java.util.List;

/**
 * @author Paweł Marszał
 *
 * Represents promotion of pawn into big piece: Queen, Knight, Rook or Bishop
 */
public class Promotion extends Move {

    /**
     * Basic move that leads to pawn being in the last row
     */
    private final BasicMove basicMove;

    /**
     * New piece, that player has chosen
     */
    private final Piece newPiece;

    public Promotion(BasicMove basicMove, PieceType newType) {
        this.basicMove = basicMove;

        this.movedPiece = basicMove.movedPiece;
        this.oldPosition = basicMove.oldPosition;
        this.newPosition = basicMove.newPosition;
        this.takenPiece = basicMove.takenPiece;
        this.takenPiecePosition = basicMove.takenPiecePosition;

        Color color = basicMove.movedPiece.getColor();
        this.newPiece = switch (newType) {
            case QUEEN -> new Queen(color);
            case ROOK -> new Rook(color);
            case BISHOP -> new Bishop(color);
            case KNIGHT -> new Knight(color);
            default -> throw new IllegalArgumentException("Cannot promote pawn to " + newType);
        };
        this.newPiece.setPosition(basicMove.newPosition);
    }

    @Override
    public void execute(InMemoryChessGame game) {
        // execute basic move
        this.basicMove.execute(game);

        // exchange pawn for new piece
        game.getCurrentPlayer().getPieces().remove(basicMove.movedPiece);
        game.board[this.basicMove.newPosition.x()][this.basicMove.newPosition.y()] = this.newPiece;
        game.getCurrentPlayer().getPieces().add(this.newPiece);
    }

    @Override
    public void undo(InMemoryChessGame game) {
        // undo exchanging pawn
        game.getCurrentPlayer().getPieces().remove(this.newPiece);
        game.board[this.basicMove.newPosition.x()][this.basicMove.newPosition.y()] = basicMove.movedPiece;
        game.getCurrentPlayer().getPieces().add(basicMove.movedPiece);

        // undo move that lead to pawn being on the last row
        this.basicMove.undo(game);
    }

    public PieceType getNewType() {
        return newPiece.getType();
    }

    @Override
    public LegalMove toDto(List<Move> legalMoves) {
        return new com.pmarshall.chessgame.model.dto.Promotion(movedPiece.getPosition(), newPosition, newPiece.getType(), withCheck, inNotation(legalMoves));
    }

    @Override
    public String inNotation(List<Move> legalMoves) {
        StringBuilder builder = new StringBuilder();

        if (basicMove.getPieceToTake() != null) {
            builder.append(basicMove.getPieceToMove().getPosition().translateX()).append("x");
        }

        builder.append(newPosition.translateX());
        builder.append(newPosition.translateY());
        builder.append("=").append(newPiece.getType());

        if (withCheck) {
            builder.append("+");
        }

        return builder.toString();
    }
}
