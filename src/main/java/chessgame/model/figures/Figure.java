package chessgame.model.figures;

import chessgame.model.game.ChessBoard;
import chessgame.model.properties.*;
import javafx.scene.image.Image;

import java.util.LinkedList;
import java.util.List;

/**
 * Interface that all figures must implement,
 * along common data fields and inner methods
 */
public abstract class Figure {

    protected final PlayerColor playerColor;
    protected Position position;
    protected boolean didNotMoveYet = true;

    protected final Image image;

    protected final List<Position> possibleMoves = new LinkedList<>();
    protected final List<Position> movesWithoutProtectingKing = new LinkedList<>();

    // Constructor
    // TODO: finish and write documentation
    public Figure(Image image, PlayerColor playerColor) {
        this.image = image;
        this.playerColor = playerColor;
    }

    // Interface - public methods to implement

    /**
     * Updates list of moves, that figure can legally make
     * @param chessboard Chessboard object to execute function on
     */
    public void updatePossibleMoves(ChessBoard chessboard) {
        updateMovesWithoutProtectingKing(chessboard);

        for (Position move : movesWithoutProtectingKing) {
            // TODO:
            //  copy chessboard
            //  simulate that move in the new chessboard
            //  check proper conditions
            //  if conditions are met, add move to the list
            if (false) {
                possibleMoves.add(move);
            }
        }
    }

    /**
     * Updates list of moves, that figure could take having ignored safety of its king
     * Needs to be implemented in each subclass
     * @param chessboard Chessboard object to execute function on
     */
    abstract public void updateMovesWithoutProtectingKing(ChessBoard chessboard);

    // Setters and Getters

    public void setPosition(Position position) {
        this.position.x = position.x;
        this.position.y = position.y;
    }

    public Position getPosition() {
        return position;
    }

    public Image getImage() {
        return image;
    }

    public void markThatFigureMoved() {
        this.didNotMoveYet = false;
    }

    public List<Position> getPossibleMoves() {
        return possibleMoves;
    }

    public List<Position> getMovesWithoutProtectingKing() {
        return movesWithoutProtectingKing;
    }

    // Inner methods for subclasses

    protected static Image loadImage(PlayerColor color, String name) {
        String path = "images/" + (color == PlayerColor.WHITE ? "white" : "black") + "-" + name + ".png";
        return new Image(path, 50, 50, false,true, false);
    }

    /**
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
     * @param chessboard Chessboard object to execute function on
     * @param directions array of pairs {horizontal step, vertical step}
     * @return list of accessible fields (all until blocked by other figure)
     * on straight lines determined by 'directions' array
     */
    protected List<Position> unlimitedMovesInGivenDirections(ChessBoard chessboard, int[][] directions) {
        List<Position> moves = new LinkedList<>();

        for (int[] dir : directions) {
            // start from closest cell in this direction
            int x = position.x + dir[0];
            int y = position.y + dir[1];

            while (validPosition(x, y)) {
                if (chessboard.board[x][y] == null) {
                    // cell is free
                    moves.add(new Position(x, y));
                } else if (chessboard.board[x][y].playerColor != this.playerColor) {
                    // enemy figure blocks path
                    moves.add(new Position(x, y));
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
     * @param chessboard Chessboard object to execute function on
     * @param jumps array of jumps - pairs of changes in position {horizontal step, vertical step}
     * @return list of fields determined by 'jumps' array, that can be occupied by figure
     */
    protected List<Position> movesViaGivenJumps(ChessBoard chessboard, int[][] jumps) {
        List<Position> moves = new LinkedList<>();

        for (int[] jump : jumps) {
            int x = position.x + jump[0];
            int y = position.y + jump[1];

            // if new position is valid and either cell is free or there is an enemy to kill
            if (validPosition(x, y) &&
                    (chessboard.board[x][y] == null || chessboard.board[x][y].playerColor != this.playerColor)) {
                moves.add(new Position(x, y));
            }
        }

        return moves;
    }
}
