package com.pmarshall.chessgame.engine.game;

import com.pmarshall.chessgame.model.dto.*;
import com.pmarshall.chessgame.engine.moves.Promotion;
import com.pmarshall.chessgame.engine.pieces.*;
import com.pmarshall.chessgame.engine.pieces.Piece;
import com.pmarshall.chessgame.model.properties.Color;
import com.pmarshall.chessgame.model.properties.MoveEffect;
import com.pmarshall.chessgame.model.properties.PieceType;
import com.pmarshall.chessgame.model.properties.Position;
import com.pmarshall.chessgame.engine.moves.Move;
import com.pmarshall.chessgame.model.service.Game;
import com.pmarshall.chessgame.model.util.Pair;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Paweł Marszał
 * <p>
 * Class that represent the totality of logical model of the game
 * It contains chessboard itself, players, and pieces.
 */
public class InMemoryChessGame implements Game {

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
    private final LinkedList<Move> moveHistory = new LinkedList<>();
    private LegalMove lastMoveDto;

    /**
     * Creates new InMemoryChessGame
     */
    public InMemoryChessGame() {

        this.board = new Piece[8][8];
        for (int rank = 0; rank < 8; rank++) {
            for (int file = 0; file < 8; file++) {
                board[rank][file] = null;
            }
        }

        initializeGame();
    }

    /**
     * Switch current player
     */
    public void changePlayer() {
        currentPlayer = currentPlayer == whitePlayer ? blackPlayer : whitePlayer;
    }

    // Setters

    // Getters

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public Player getOtherPlayer() {
        return currentPlayer == whitePlayer ? blackPlayer : whitePlayer;
    }

    private Player getPlayerByColor(Color color) {
        return currentPlayer.getColor() == color ? currentPlayer : getOtherPlayer();
    }

    public Move getLastMove() {
        if (moveHistory.isEmpty())
            return null;
        return moveHistory.getLast();
    }

    /**
     *
     */
    public void removePiece(Piece piece) {
        getPlayerByColor(piece.getColor()).getPieces().remove(piece);
        board[piece.getPosition().rank()][piece.getPosition().file()] = null;
    }

    public void addPieceBack(Piece piece, Position pos) {
        getPlayerByColor(piece.getColor()).getPieces().add(piece);
        board[pos.rank()][pos.file()] = piece;
    }

    private boolean isIllegalMove(Position from, Position to) {
        Piece piece = board[from.rank()][from.file()];
        if (piece == null)
            return true;

        return piece.findMoveByTargetPosition(to) == null;
    }

    private boolean isIllegalMove(Position from, Position to, PieceType promotion) {
        Piece piece = board[from.rank()][from.file()];
        if (piece == null)
            return true;

        return piece.findPromotionByTargetPositionAndType(to, promotion) == null;
    }

    @Override
    public Color currentPlayer() {
        return currentPlayer.getColor();
    }

    @Override
    public LegalMove lastMove() {
        return lastMoveDto;
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
    public com.pmarshall.chessgame.model.dto.Piece[][] getBoardWithPieces() {
        com.pmarshall.chessgame.model.dto.Piece[][] result = new com.pmarshall.chessgame.model.dto.Piece[8][8];
        for (int rank = 0; rank < 8; rank++) {
            for (int file = 0; file < 8; file++) {
                Piece piece = board[rank][file];
                if (piece != null) {
                    result[rank][file] = new com.pmarshall.chessgame.model.dto.Piece(piece.getType(), piece.getColor());
                }
            }
        }
        return result;
    }

    @Override
    public com.pmarshall.chessgame.model.dto.Piece getPiece(Position on) {
        Piece piece = board[on.rank()][on.file()];
        if (piece == null)
            return null;

        return new com.pmarshall.chessgame.model.dto.Piece(piece.getType(), piece.getColor());
    }

    public List<LegalMove> legalMoves() {
        List<Move> legalMoves = currentPlayer.getAllPossibleMoves();
        return legalMoves.stream().map(move -> move.toDto(legalMoves)).collect(Collectors.toList());
    }

    @Override
    public Collection<Position> legalMovesFrom(Position from) {
        Piece piece = board[from.rank()][from.file()];
        if (piece == null)
            return Collections.emptyList();

        return piece.getPossibleMoves().stream().map(Move::getNewPosition).distinct().collect(Collectors.toList());
    }

    @Override
    public boolean isMoveLegal(Position from, Position to) {
        Piece piece = board[from.rank()][from.file()];
        if (piece == null)
            return false;

        return piece.getPossibleMoves().stream().anyMatch(m -> m.getNewPosition().equals(to));
    }

    @Override
    public boolean isPromotionRequired(Position from, Position to) {
        Piece piece = board[from.rank()][from.file()];
        if (piece == null)
            return false;

        return piece.getPossibleMoves().stream().anyMatch(m -> m.getNewPosition().equals(to) && m instanceof Promotion);
    }

    @Override
    public boolean executeMove(Position from, Position to) {
        if (isIllegalMove(from, to))
            return false;

        Piece piece = board[from.rank()][from.file()];
        executeMove(piece.findMoveByTargetPosition(to));
        return true;
    }

    @Override
    public boolean executeMove(Position from, Position to, PieceType promotion) {
        if (isIllegalMove(from, to, promotion))
            return false;

        Piece piece = board[from.rank()][from.file()];
        executeMove(piece.findPromotionByTargetPositionAndType(to, promotion));
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
        lastMoveDto = move.toDto(currentPlayer.getAllPossibleMoves());

        move.execute(this);
        moveHistory.addLast(move);

        changePlayer();

        // List of pieces is copied because simulations temporarily modify it,
        // which causes ConcurrentModificationException to be thrown
        List.copyOf(currentPlayer.getPieces()).forEach(f -> f.updatePossibleMoves(this));

        // check conditions of victory
        if (lastMoveDto.moveEffect() == MoveEffect.CHECKMATE) {
            winner = getOtherPlayer();
        } else if (lastMoveDto.moveEffect() == MoveEffect.STALEMATE) {
            draw = true;
        }
    }

    /**
     * Simulates execution of move, to test if it is safe and if it puts the enemy in check
     * @param move move to test
     * @return true if move can be executed safely, false if it would endanger the king
     */
    public boolean simulateMove(Move move) {
        move.execute(this);
        moveHistory.addLast(move);

        getOtherPlayer().getPieces().forEach(p -> p.updateMovesWithoutProtectingKing(this));
        boolean isLegal = !isPosThreatened(currentPlayer.getKing().getPosition(), getOtherPlayer());

        if (isLegal) {
            getCurrentPlayer().getPieces().forEach(p -> p.updateMovesWithoutProtectingKing(this));
            getOtherPlayer().getPieces().forEach(p -> p.updatePossibleMoves(this));

            boolean opponentInCheck = isPosThreatened(getOtherPlayer().getKing().getPosition(), currentPlayer);
            boolean opponentCanMove = !getOtherPlayer().getAllPossibleMoves().isEmpty();

            MoveEffect moveEffect = opponentCanMove
                    ? (opponentInCheck ? MoveEffect.CHECK : MoveEffect.NONE)
                    : (opponentInCheck ? MoveEffect.STALEMATE : MoveEffect.CHECKMATE);

            move.setMoveEffect(moveEffect);
        }

        moveHistory.removeLast();
        move.undo(this);

        return isLegal;
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
     * @return null if no one is winner yet, or reference to winner if there is one already
     */
    public Player getWinner() {
        return winner;
    }

    /**
     * @return true if game has ended in a draw, false otherwise
     */
    public boolean isDraw() {
        return draw;
    }

    /**
     * Inserts piece into the game - used in #initializeGame()
     * @param piece piece to be inserted
     * @param player new piece's owner
     * @param position where piece should be inserted
     */
    private void addNewPiece(Piece piece, Player player, Position position) {
        board[position.rank()][position.file()] = piece;
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
        for (int file = 0; file < 8; file++) {
            addNewPiece(new Pawn(Color.WHITE), whitePlayer, new Position(6, file));
        }
        addNewPiece(new Rook(Color.WHITE), whitePlayer, new Position(7, 0));
        addNewPiece(new Knight(Color.WHITE), whitePlayer, new Position(7, 1));
        addNewPiece(new Bishop(Color.WHITE), whitePlayer, new Position(7, 2));
        addNewPiece(new Queen(Color.WHITE), whitePlayer, new Position(7, 3));

        King whiteKing = new King(Color.WHITE);
        addNewPiece(whiteKing, whitePlayer, new Position(7, 4));
        whitePlayer.setKing(whiteKing);

        addNewPiece(new Bishop(Color.WHITE), whitePlayer, new Position(7, 5));
        addNewPiece(new Knight(Color.WHITE), whitePlayer, new Position(7, 6));
        addNewPiece(new Rook(Color.WHITE), whitePlayer, new Position(7, 7));

        // BLACKS
        for (int file = 0; file < 8; file++) {
            addNewPiece(new Pawn(Color.BLACK), blackPlayer, new Position(1, file));
        }
        addNewPiece(new Rook(Color.BLACK), blackPlayer, new Position(0, 0));
        addNewPiece(new Knight(Color.BLACK), blackPlayer, new Position(0, 1));
        addNewPiece(new Bishop(Color.BLACK), blackPlayer, new Position(0, 2));
        addNewPiece(new Queen(Color.BLACK), blackPlayer, new Position(0, 3));

        King blackKing = new King(Color.BLACK);
        addNewPiece(blackKing, blackPlayer, new Position(0, 4));
        blackPlayer.setKing(blackKing);

        addNewPiece(new Bishop(Color.BLACK), blackPlayer, new Position(0, 5));
        addNewPiece(new Knight(Color.BLACK), blackPlayer, new Position(0, 6));
        addNewPiece(new Rook(Color.BLACK), blackPlayer, new Position(0, 7));

        // Calculate initial moves
        currentPlayer = whitePlayer;
        getOtherPlayer().getPieces().forEach(f -> f.updateMovesWithoutProtectingKing(this));
        currentPlayer.getPieces().forEach(f -> f.updatePossibleMoves(this));
    }

}
