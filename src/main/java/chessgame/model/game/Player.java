package chessgame.model.game;

import chessgame.model.pieces.Piece;
import chessgame.model.pieces.King;
import chessgame.model.game.moves.Move;
import chessgame.model.properties.PlayerColor;

import java.util.LinkedList;
import java.util.List;

public class Player {
    private final PlayerColor playerColor;

    private final List<Piece> pieces = new LinkedList<>();
    private King king;

    public Player(PlayerColor playerColor) {
        this.playerColor = playerColor;
    }

    public List<Move> getAllPossibleMoves() {
        List<Move> moves = new LinkedList<>();
        pieces.forEach(figure -> moves.addAll(figure.getPossibleMoves()));
        return moves;
    }

    public List<Piece> getPieces() {
        return this.pieces;
    }

    public void setKing(King king) {
        this.king = king;
    }

    public King getKing() {
        return this.king;
    }

    public boolean kingIsChecked() {
        return this.king.getIsChecked();
    }

    public PlayerColor getColor() {
        return this.playerColor;
    }

    public String getSignature() {
        return this.playerColor == PlayerColor.WHITE ? "WHITES" : "BLACKS";
    }
}
