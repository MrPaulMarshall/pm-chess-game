package com.pmarshall.chessgame.engine.game;

import com.pmarshall.chessgame.engine.moves.Move;
import com.pmarshall.chessgame.engine.pieces.King;
import com.pmarshall.chessgame.engine.pieces.Piece;
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
        return color;
    }

    public List<Piece> getPieces() {
        return pieces;
    }

    public King getKing() {
        return king;
    }


    public boolean isKingChecked() {
        return king.getIsChecked();
    }

    @Override
    public String toString() {
        return color == Color.WHITE ? "WHITES" : "BLACKS";
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
