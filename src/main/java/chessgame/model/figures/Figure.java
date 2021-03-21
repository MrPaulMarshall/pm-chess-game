package chessgame.model.figures;

import chessgame.model.game.Game;
import chessgame.model.game.moves.BasicMove;
import chessgame.model.game.moves.Move;
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

    protected final List<Move> possibleMoves = new LinkedList<>();
    protected final List<Move> movesWithoutProtectingKing = new LinkedList<>();

    // Constructor
    // TODO: finish and write documentation
    public Figure(Image image, PlayerColor playerColor) {
        this.image = image;
        this.playerColor = playerColor;
    }

    // Interface - public methods to implement

    /**
     * Updates list of moves, that figure can legally make
     * @param game Chessboard object to execute function on
     */
    public void updatePossibleMoves(Game game) {
        possibleMoves.clear();
        updateMovesWithoutProtectingKing(game);

        game.setRealChessboard(false);

        for (Move move : movesWithoutProtectingKing) {
            if (!game.simulateMove(move)) {
                possibleMoves.add(move);
            }
        }

        game.setRealChessboard(true);
    }

    /**
     * Updates list of moves, that figure could take having ignored safety of its king
     * Needs to be implemented in each subclass
     * @param game Chessboard object to execute function on
     */
    abstract public void updateMovesWithoutProtectingKing(Game game);

    // Setters and Getters

    public void setPosition(Position position) {
        this.position = new Position(position.x, position.y);
    }

    public Position getPosition() {
        return position;
    }

    public Image getImage() {
        return image;
    }

    public PlayerColor getColor() {
        return this.playerColor;
    }

    public boolean getMovedFlag() {
        return this.didNotMoveYet;
    }

    public void markThatFigureMoved() {
        this.didNotMoveYet = false;
    }

    public void undoMarkThatFigureMoved() {
        this.didNotMoveYet = true;
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

    public Move findMoveByTargetPosition(Position newPosition) {
        return this.possibleMoves.stream().filter(p -> p.getNewPosition().equals(newPosition))
                .findFirst().orElse(null);
    }
}
