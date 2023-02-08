package com.pmarshall.chessgame.engine.pieces;

import com.pmarshall.chessgame.engine.moves.Promotion;
import com.pmarshall.chessgame.engine.properties.Color;
import com.pmarshall.chessgame.engine.properties.PieceType;
import com.pmarshall.chessgame.engine.properties.Position;
import com.pmarshall.chessgame.engine.game.InMemoryChessGame;
import com.pmarshall.chessgame.engine.moves.BasicMove;
import com.pmarshall.chessgame.engine.moves.Move;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Paweł Marszał
 * <p>
 * Interface that all figures must implement.
 * <p>
 * It also contains data fields and inner methods common for all (or most) of its subclasses.
 */
public abstract class Piece {

    /**
     * Color of the piece (and its owner)
     */
    protected final Color color;
    /**
     * Position of the piece on the board
     */
    protected Position position;
    /**
     * Flag that tells if piece remains unmoved
     * It's important for double start (pawn) and castling (King and Rook)
     */
    protected boolean didNotMoveYet = true;

    /**
     * Collection of all legal moves that piece can do
     */
    protected final List<Move> possibleMoves = new LinkedList<>();
    /**
     * Collection of all legal moves if safety of own king is not considered
     */
    protected final List<Move> movesWithoutProtectingKing = new LinkedList<>();

    /**
     * Code common for all piece's constructors
     *
     * @param playerColor color of piece's owner
     */
    public Piece(Color playerColor) {
        this.color = playerColor;
        this.position = null;
    }

    // Interface - public methods to implement

    /**
     * Updates list of moves, that figure can legally make
     *
     * @param game InMemoryChessGame object, providing context for updating moves
     */
    public void updatePossibleMoves(InMemoryChessGame game) {
        possibleMoves.clear();
        updateMovesWithoutProtectingKing(game);

        for (Move move : List.copyOf(movesWithoutProtectingKing)) {
            if (game.simulateMove(move)) {
                possibleMoves.add(move);
            }
        }
    }

    /**
     * Updates list of moves, that figure could take having ignored safety of its king
     * Needs to be implemented in each subclass
     *
     * @param game InMemoryChessGame object, providing context for updating moves
     */
    abstract public void updateMovesWithoutProtectingKing(InMemoryChessGame game);

    public abstract PieceType getType();

    /**
     * @return representation of piece in 'chess notation'
     */
    public abstract String toString();

    // Setters

    public void setPosition(Position position) {
        this.position = position;
    }

    public void markThatFigureMoved() {
        this.didNotMoveYet = false;
    }

    public void undoMarkThatFigureMoved() {
        this.didNotMoveYet = true;
    }


    // Getters

    public Position getPosition() {
        return position;
    }

    public Color getColor() {
        return color;
    }

    public boolean getDidNotMoveFlag() {
        return didNotMoveYet;
    }

    public List<Move> getPossibleMoves() {
        return possibleMoves;
    }

    public List<Move> getMovesWithoutProtectingKing() {
        return movesWithoutProtectingKing;
    }


    // Inner methods for subclasses

    /**
     * Validates given coordinates
     *
     * @param x horizontal coordinate
     * @param y vertical coordinate
     * @return true if given position is on the board, or false if it is out of bounds
     */
    protected static boolean validPosition(int x, int y) {
        return (x >= 0 && x <= 7) && (y >= 0 && y <= 7);
    }

    /**
     * Used by classes Bishop, Rook and Queen
     *
     * @param game       Game object to execute function on
     * @param directions array of pairs {horizontal step, vertical step}
     * @return list of accessible fields (all until blocked by other figure)
     * on straight lines determined by 'directions' array
     */
    protected List<Move> unlimitedMovesInGivenDirections(InMemoryChessGame game, int[][] directions) {
        List<Move> moves = new LinkedList<>();

        for (int[] dir : directions) {
            // start from the closest cell in this direction
            int rank = position.rank() + dir[1];
            int file = position.file() + dir[0];

            while (validPosition(rank, file)) {
                if (game.board[rank][file] == null) {
                    // cell is free
                    moves.add(new BasicMove(
                            this, new Position(rank, file), null, null));
                } else if (game.board[rank][file].color != color) {
                    // enemy figure blocks path
                    moves.add(new BasicMove(
                            this, new Position(rank, file), game.board[rank][file], new Position(rank, file)));
                    break;
                } else {
                    // allied figure blocks path
                    break;
                }

                // go to next cell
                rank += dir[1];
                file += dir[0];
            }
        }

        return moves;
    }

    /**
     * Used by classes Knight and King
     *
     * @param game  Game object to execute function on
     * @param jumps array of jumps - pairs of changes in position {horizontal step, vertical step}
     * @return list of fields determined by 'jumps' array, that can be occupied by figure
     */
    protected List<Move> movesViaGivenJumps(InMemoryChessGame game, int[][] jumps) {
        List<Move> moves = new LinkedList<>();

        for (int[] jump : jumps) {
            int rank = position.rank() + jump[1];
            int file = position.file() + jump[0];

            // if new position is valid and either cell is free or there is an enemy to kill
            if (validPosition(rank, file) &&
                    (game.board[rank][file] == null || game.board[rank][file].color != color)) {

                moves.add(new BasicMove(this, new Position(rank, file), game.board[rank][file],
                        game.board[rank][file] != null ? new Position(rank, file) : null));
            }
        }

        return moves;
    }

    /**
     * Method used to check if player has chosen existing, legal move
     *
     * @param newPosition position chosen by the player
     * @return legal move that results in currently chosen piece landing on given position.
     *         Promotions are excluded, because they need new type to uniquely identify them
     */
    public Move findMoveByTargetPosition(Position newPosition) {
        return possibleMoves.stream()
                .filter(m -> m.getNewPosition().equals(newPosition))
                .filter(m -> !(m instanceof Promotion))
                .findFirst().orElse(null);
    }

    public Promotion findPromotionByTargetPositionAndType(Position newPosition, PieceType newType) {
        return possibleMoves.stream()
                .filter(m -> m instanceof Promotion)
                .map(Promotion.class::cast)
                .filter(p -> p.getNewPosition().equals(newPosition))
                .filter(p -> p.getNewType() == newType)
                .findFirst().orElse(null);
    }
}
