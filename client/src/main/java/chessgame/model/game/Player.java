package chessgame.model.game;

import chessgame.model.pieces.Piece;
import chessgame.model.pieces.King;
import chessgame.model.moves.Move;
import chessgame.model.properties.PlayerColor;

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
    private final PlayerColor playerColor;

    /**
     * Stores all pieces left to the player
     */
    private final List<Piece> pieces = new LinkedList<>();
    /**
     * Direct reference to the king
     */
    private King king;

    public Player(PlayerColor playerColor) {
        this.playerColor = playerColor;
    }

    // Getters

    public PlayerColor getColor() {
        return this.playerColor;
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
        return this.playerColor == PlayerColor.WHITE ? "WHITES" : "BLACKS";
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
