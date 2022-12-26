package com.pmarshall.chessgame.model.game;

import com.pmarshall.chessgame.model.moves.Move;
import com.pmarshall.chessgame.model.pieces.King;
import com.pmarshall.chessgame.model.pieces.Piece;
import com.pmarshall.chessgame.model.properties.Color;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Paweł Marszał
 *
 * Represents player
 */
public class Player {
    /**
     * Stores color: {white, black}
     */
    private final Color color;

    /**
     * Stores all pieces left to the player
     */
    private final List<Piece> pieces = new LinkedList<>();
    /**
     * Direct reference to the king
     */
    private King king;

    public Player(Color color) {
        this.color = color;
    }

    // Getters

    public Color getColor() {
        return this.color;
    }

    public List<Piece> getPieces() {
        return this.pieces;
    }

    public King getKing() {
        return this.king;
    }


    public boolean isKingChecked() {
        return this.king.getIsChecked();
    }

    @Override
    public String toString() {
        return this.color == Color.WHITE ? "WHITES" : "BLACKS";
    }

    /**
     * @return combined list of legal moves by that player's pieces
     */
    public List<Move> getAllPossibleMoves() {
        List<Move> moves = new LinkedList<>();
        pieces.forEach(figure -> moves.addAll(figure.getPossibleMoves()));
        return moves;
    }

    // Setters

    public void setKing(King king) {
        this.king = king;
    }

}
