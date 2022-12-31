package com.pmarshall.chessgame.model.game;

import com.pmarshall.chessgame.model.moves.Promotion;
import com.pmarshall.chessgame.model.pieces.*;
import com.pmarshall.chessgame.model.properties.Color;
import com.pmarshall.chessgame.model.properties.PieceType;
import com.pmarshall.chessgame.model.properties.Position;
import com.pmarshall.chessgame.model.moves.Move;
import com.pmarshall.chessgame.model.service.Chessboard;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import java.util.Collection;

/**
 * @author Paweł Marszał
 * <p>
 * Class that represent the totality of logical model of the game
 * It contains chessboard itself, players, and pieces.
 */
public class Game implements Chessboard {
    /**
     * If true, game is proceeding normally
     * If false, game is in simulation mode, in order to check if given move would leave own king in check
     */
    private boolean gameMode;

    /**
     * Reference to controller, needed to ask player for piece during promotion
     */
    private PieceType promotedPiece;

    /**
     * Array that represents board itself
     * It contains pieces where there are such, and null elsewhere
     */
    public final Piece[][] board;

    /**
     * References to players. 'currentPlayer' is changing between whitePlayer and blackPlayer
     */
    private Player currentPlayer;
    private Player whitePlayer;
    private Player blackPlayer;

    /**
     * Reference to player that has already won, or null otherwise
     */
    private Player winner;
    /**
     * True if game has ended in a draw, false otherwise
     */
    private boolean draw;

    /**
     * Reference to last executed move, used by log and capturing EnPassant
     */
    private Move lastMove;

    /**
     * Creates new Game
     */
    public Game() {
        this.gameMode = true;

        this.board = new Piece[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                this.board[i][j] = null;
            }
        }

        this.initializeGame();
    }

    /**
     * Switch current player
     */
    public void changePlayer() {
        this.currentPlayer = this.currentPlayer == this.whitePlayer ? this.blackPlayer : this.whitePlayer;
    }

    // Setters

    public void setGameMode(boolean gameMode) {
        this.gameMode = gameMode;
    }

    // Getters

    public Piece getPiece(int i, int j) {
        return this.board[i][j];
    }

    public Player getCurrentPlayer() {
        return this.currentPlayer;
    }

    public Player getOtherPlayer() {
        return this.currentPlayer == this.whitePlayer ? this.blackPlayer : this.whitePlayer;
    }

    private Player getPlayerByColor(Color color) {
        return this.currentPlayer.getColor() == color ? this.currentPlayer : this.getOtherPlayer();
    }

    public Move getLastMove() {
        return this.lastMove;
    }

    public boolean getGameMode() {
        return this.gameMode;
    }

    /**
     *
     */
    public void removePiece(Piece piece) {
        this.getPlayerByColor(piece.getColor()).getPieces().remove(piece);
        this.board[piece.getPosition().x()][piece.getPosition().y()] = null;
    }

    public void addPieceBack(Piece piece, Position pos) {
        this.getPlayerByColor(piece.getColor()).getPieces().add(piece);
        this.board[pos.x()][pos.y()] = piece;
    }

    private boolean isIllegalMove(Position from, Position to, PieceType promotion) {
        Piece piece = this.board[from.x()][from.y()];
        if (piece == null)
            return true;
        Move move = piece.findMoveByTargetPosition(to);
        if (move == null)
            return true;

        if (move instanceof Promotion) {
            if (promotion == null)
                return true;
            return !switch (promotion) {
                case QUEEN, ROOK, BISHOP, KNIGHT -> true;
                default -> false;
            };
        } else {
            return promotion != null;
        }
    }

    @Override
    public Color currentPlayer() {
        return currentPlayer.getColor();
    }

    @Override
    public boolean activeCheck() {
        return currentPlayer.isKingChecked();
    }

    @Override
    public boolean gameEnded() {
        return draw || winner != null;
    }

    @Override
    public String lastMoveInNotation() {
        return lastMove.toString();
    }

    @Override
    public Pair<Color, String> outcome() {
        if (draw)
            return Pair.of(null, "Stalemate");
        if (winner != null)
            return Pair.of(winner.getColor(), "Checkmate");
        return null;
    }

    @Override
    public Pair<PieceType, Color>[][] getBoardWithPieces() {
        Pair<PieceType, Color>[][] result = new Pair[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece piece = board[i][j];
                if (piece != null) {
                    result[i][j] = Pair.of(piece.getType(), piece.getColor());
                }
            }
        }
        return result;
    }

    @Override
    public Collection<Triple<Position, Position, Boolean>> legalMoves() {
        return currentPlayer.getAllPossibleMoves().stream()
                .map(move -> Triple.of(
                        move.getPieceToMove().getPosition(), move.getNewPosition(), move instanceof Promotion))
                .toList();
    }

    @Override
    public boolean executeMove(Position from, Position to) {
        if (isIllegalMove(from, to, null))
            return false;

        Piece piece = this.board[from.x()][from.y()];
        executeMove(piece.findMoveByTargetPosition(to));
        return true;
    }

    @Override
    public boolean executeMove(Position from, Position to, PieceType promotion) {
        if (isIllegalMove(from, to, promotion))
            return false;

        promotedPiece = promotion;
        Piece piece = this.board[from.x()][from.y()];
        executeMove(piece.findMoveByTargetPosition(to));
        return true;
    }

    /**
     * Executes move in full, that means it takes care of:
     * - changing position(s) of pieces
     * - taking of enemy piece
     * - actualizing history
     * - recalculating possible moves
     * - checking conditions of victory (draw)
     * - changing current player
     * @param move move to execute
     */
    public void executeMove(Move move) {
        // potentially remove enemy piece
        if (move.getPieceToTake() != null) {
            Position posOfTakenPiece = move.getPieceToTake().getPosition();
            this.board[posOfTakenPiece.x()][posOfTakenPiece.y()] = null;
            this.getOtherPlayer().getPieces().remove(move.getPieceToTake());
        }

        // move piece to new position
        move.execute(this);
        this.lastMove = move;

        // update moves (without concern for king's safety)
        this.currentPlayer.getPieces().forEach(p -> p.updateMovesWithoutProtectingKing(this));
        // check is enemy king is now threatened
        this.getOtherPlayer().getKing().setIsChecked(
                this.isPosThreatened(this.getOtherPlayer().getKing().getPosition(), this.currentPlayer));

        // change player
        this.changePlayer();

        // calculate possible moves
        this.currentPlayer.getPieces().forEach(f -> f.updatePossibleMoves(this));

        // check conditions of victory
        if (this.currentPlayer.getAllPossibleMoves().isEmpty()) {
            if (this.currentPlayer.isKingChecked()) {
                this.winner = this.getOtherPlayer();
            }
            else {
                this.draw = true;
            }
        }
    }

    /**
     * Simulates execution of move, to test if it is safe
     * @param move move to test
     * @return true if move can be executed safely, false if it would endanger the king
     */
    public boolean simulateMove(Move move) {
        this.gameMode = false;
        Move trulyLastMove = this.lastMove;

        // potentially remove enemy piece
        if (move.getPieceToTake() != null) {
            Position posOfTakenPiece = move.getPieceToTake().getPosition();
            this.board[posOfTakenPiece.x()][posOfTakenPiece.y()] = null;
            this.getOtherPlayer().getPieces().remove(move.getPieceToTake());
        }

        // move piece to new position
        move.execute(this);
        this.lastMove = move;

        // update moves (without concern for king's safety)
        this.getOtherPlayer().getPieces().forEach(p -> p.updateMovesWithoutProtectingKing(this));
        // check is current king is now threatened
        boolean isKingUnderCheck = this.isPosThreatened(this.currentPlayer.getKing().getPosition(), this.getOtherPlayer());

        // undo move
        this.lastMove = trulyLastMove;
        move.undo(this);

        // potentially return enemy piece
        if (move.getPieceToTake() != null) {
            Position posOfTakenPiece = move.getPieceToTake().getPosition();
            this.board[posOfTakenPiece.x()][posOfTakenPiece.y()] = move.getPieceToTake();
            this.getOtherPlayer().getPieces().add(move.getPieceToTake());
        }

        this.gameMode = true;
        return !isKingUnderCheck;
    }

    /**
     * @param position position to be checked
     * @param player player whose pieces would be threatening position
     * @return true if position is threaten by player's pieces, false otherwise
     */
    public boolean isPosThreatened(Position position, Player player) {
        return player.getPieces().stream().anyMatch(
                piece -> piece.getMovesWithoutProtectingKing().stream().anyMatch(m -> m.getNewPosition().equals(position))
        );
    }

    /**
     * Asks controller to obtain new piece from player
     * @return piece chosen by player
     */
    public Piece askForPromotedPiece() {
        Color color = currentPlayer.getColor();
        return switch (promotedPiece) {
            case QUEEN -> new Queen(color);
            case ROOK -> new Rook(color);
            case BISHOP -> new Bishop(color);
            case KNIGHT -> new Knight(color);
            default -> throw new IllegalStateException("Should never happen");
        };
    }

    /**
     * @return null if no one is winner yet, or reference to winner if there is one already
     */
    public Player getWinner() {
        return this.winner;
    }

    /**
     * @return true if game has ended in a draw, false otherwise
     */
    public boolean isDraw() {
        return this.draw;
    }

    /**
     * Inserts piece into the game - used in #initializeGame()
     * @param piece piece to be inserted
     * @param player new piece's owner
     * @param position where piece should be inserted
     */
    private void addNewPiece(Piece piece, Player player, Position position) {
        this.board[position.x()][position.y()] = piece;
        piece.setPosition(position);
        player.getPieces().add(piece);
    }

    /**
     * Initializes game:
     * - creates players
     * - creates and inserts pieces
     * - sets initial values of flags and parameters
     * - calculates possible moves in the first round
     */
    private void initializeGame() {
        // Create players
        this.whitePlayer = new Player(Color.WHITE);
        this.blackPlayer = new Player(Color.BLACK);

        // WHITES
        for (int i = 0; i < 8; i++) {
            addNewPiece(new Pawn(Color.WHITE), this.whitePlayer, new Position(i, 6));
        }
        addNewPiece(new Rook(Color.WHITE), this.whitePlayer, new Position(0, 7));
        addNewPiece(new Knight(Color.WHITE), this.whitePlayer, new Position(1, 7));
        addNewPiece(new Bishop(Color.WHITE), this.whitePlayer, new Position(2, 7));
        addNewPiece(new Queen(Color.WHITE), this.whitePlayer, new Position(3, 7));

        King whiteKing = new King(Color.WHITE);
        addNewPiece(whiteKing, this.whitePlayer, new Position(4, 7));
        this.whitePlayer.setKing(whiteKing);

        addNewPiece(new Bishop(Color.WHITE), this.whitePlayer, new Position(5, 7));
        addNewPiece(new Knight(Color.WHITE), this.whitePlayer, new Position(6, 7));
        addNewPiece(new Rook(Color.WHITE), this.whitePlayer, new Position(7, 7));

        // BLACKS
        for (int i = 0; i < 8; i++) {
            addNewPiece(new Pawn(Color.BLACK), this.blackPlayer, new Position(i, 1));
        }
        addNewPiece(new Rook(Color.BLACK), this.blackPlayer, new Position(0, 0));
        addNewPiece(new Knight(Color.BLACK), this.blackPlayer, new Position(1, 0));
        addNewPiece(new Bishop(Color.BLACK), this.blackPlayer, new Position(2, 0));
        addNewPiece(new Queen(Color.BLACK), this.blackPlayer, new Position(3, 0));

        King blackKing = new King(Color.BLACK);
        addNewPiece(blackKing, this.blackPlayer, new Position(4, 0));
        this.blackPlayer.setKing(blackKing);

        addNewPiece(new Bishop(Color.BLACK), this.blackPlayer, new Position(5, 0));
        addNewPiece(new Knight(Color.BLACK), this.blackPlayer, new Position(6, 0));
        addNewPiece(new Rook(Color.BLACK), this.blackPlayer, new Position(7, 0));

        // Set initial flags
        this.winner = null;
        this.draw = false;
        this.lastMove = null;

        // Calculate initial moves
        this.currentPlayer = this.whitePlayer;
        this.getOtherPlayer().getPieces().forEach(f -> f.updateMovesWithoutProtectingKing(this));
        this.currentPlayer.getPieces().forEach(f -> f.updatePossibleMoves(this));
    }

}
