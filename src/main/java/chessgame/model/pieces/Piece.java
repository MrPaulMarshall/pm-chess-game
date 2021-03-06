package chessgame.model.pieces;

import chessgame.model.game.Game;
import chessgame.model.moves.BasicMove;
import chessgame.model.moves.Move;
import chessgame.model.properties.*;
import javafx.scene.image.Image;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Paweł Marszał
 *
 * Interface that all figures must implement.
 *
 * It also contains data fields and inner methods common for all (or most) of its subclasses.
 */
public abstract class Piece {

    /**
     * Color of the piece (and its owner)
     */
    protected final PlayerColor playerColor;
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
     * Object that stores loaded image of piece, used to display piece in GUI
     */
    protected final Image image;

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
     * @param image loaded piece's image
     * @param playerColor color of piece's owner
     */
    public Piece(Image image, PlayerColor playerColor) {
        this.image = image;
        this.playerColor = playerColor;
        this.position = null;
    }

    // Interface - public methods to implement

    /**
     * Updates list of moves, that figure can legally make
     * @param game Game object, providing context for updating moves
     */
    public void updatePossibleMoves(Game game) {
        possibleMoves.clear();
        updateMovesWithoutProtectingKing(game);

        for (Move move : movesWithoutProtectingKing) {
            if (game.simulateMove(move)) {
                possibleMoves.add(move);
            }
        }
    }

    /**
     * Updates list of moves, that figure could take having ignored safety of its king
     * Needs to be implemented in each subclass
     * @param game Game object, providing context for updating moves
     */
    abstract public void updateMovesWithoutProtectingKing(Game game);

    /**
     * @return representation of piece in 'chess notation'
     */
    public abstract String toString();

    // Setters

    public void setPosition(Position position) {
        this.position = new Position(position.x, position.y);
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

    public Image getImage() {
        return image;
    }

    public PlayerColor getColor() {
        return this.playerColor;
    }

    public boolean getDidNotMoveFlag() {
        return this.didNotMoveYet;
    }

    public List<Move> getPossibleMoves() {
        return possibleMoves;
    }

    public List<Move> getMovesWithoutProtectingKing() {
        return movesWithoutProtectingKing;
    }


    // Inner methods for subclasses

    protected static Image loadImage(PlayerColor color, String name) {
        String path = "images/" + (color == PlayerColor.WHITE ? "white" : "black") + "-" + name + ".png";
        return new Image(path, 50, 50, false,true, false);
    }

    /**
     * Validates given coordinates
     * @param x horizontal coordinate
     * @param y vertical coordinate
     * @return true if given position is on the board, or false if it is out of bounds
     */
    protected static boolean validPosition(int x, int y) {
        return (x >= 0 && x <= 7) && (y >= 0 && y <= 7);
    }

    /**
     * Used by classes Bishop, Rook and Queen
     * @param game Chessboard object to execute function on
     * @param directions array of pairs {horizontal step, vertical step}
     * @return list of accessible fields (all until blocked by other figure)
     * on straight lines determined by 'directions' array
     */
    protected List<Move> unlimitedMovesInGivenDirections(Game game, int[][] directions) {
        List<Move> moves = new LinkedList<>();

        for (int[] dir : directions) {
            // start from closest cell in this direction
            int x = position.x + dir[0];
            int y = position.y + dir[1];

            while (validPosition(x, y)) {
                if (game.board[x][y] == null) {
                    // cell is free
                    moves.add(new BasicMove(
                            this, new Position(x, y), null, null));
                } else if (game.board[x][y].playerColor != this.playerColor) {
                    // enemy figure blocks path
                    moves.add(new BasicMove(
                            this, new Position(x, y), game.board[x][y], new Position(x, y)));
                    break;
                } else {
                    // allied figure blocks path
                    break;
                }

                // go to next cell
                x += dir[0];
                y += dir[1];
            }
        }

        return moves;
    }

    /**
     * Used by classes Knight and King
     * @param game Chessboard object to execute function on
     * @param jumps array of jumps - pairs of changes in position {horizontal step, vertical step}
     * @return list of fields determined by 'jumps' array, that can be occupied by figure
     */
    protected List<Move> movesViaGivenJumps(Game game, int[][] jumps) {
        List<Move> moves = new LinkedList<>();

        for (int[] jump : jumps) {
            int x = position.x + jump[0];
            int y = position.y + jump[1];

            // if new position is valid and either cell is free or there is an enemy to kill
            if (validPosition(x, y) &&
                    (game.board[x][y] == null || game.board[x][y].playerColor != this.playerColor)) {

                moves.add(new BasicMove(this, new Position(x, y), game.board[x][y],
                        game.board[x][y] != null ? new Position(x, y) : null));
            }
        }

        return moves;
    }

    /**
     * Method used to check if player has chosen existing, legal move
     * @param newPosition position chosen by the player
     * @return legal move that results in currently chosen piece landing on given position
     * rules of chess guarantee that at most one such move can exist
     */
    public Move findMoveByTargetPosition(Position newPosition) {
        return this.possibleMoves.stream().filter(p -> p.getNewPosition().equals(newPosition))
                .findFirst().orElse(null);
    }

}
