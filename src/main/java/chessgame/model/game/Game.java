package chessgame.model.game;

import chessgame.controller.BoardScreenController;
import chessgame.model.pieces.*;
import chessgame.model.game.moves.Move;
import chessgame.model.properties.PlayerColor;
import chessgame.model.properties.Position;

import java.util.LinkedList;
import java.util.List;

/**
 * Class that represent the game;
 * It contains chessboard itself, players, and pieces.<br>
 *
 * In order to check whether move is valid (doesn't endanger a king)
 * this object can be copied, and simulation can be performed on that copy
 */
public class Game {
    private boolean realChessboard;
    private final BoardScreenController boardScreenController;

    public final Piece[][] board;

    private Player currentPlayer;
    private Player whitePlayer;
    private Player blackPlayer;

    private final List<Move> possibleMoves = new LinkedList<>();

    private Player winner;
    private boolean draw;

    private Move lastMove;

    /**
     *
     * @param realChessboard
     * @param boardScreenController
     */
    public Game(boolean realChessboard, BoardScreenController boardScreenController) {
        this.realChessboard = realChessboard;
        this.boardScreenController = boardScreenController;

        this.board = new Piece[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                this.board[i][j] = null;
            }
        }

        if (realChessboard) {
            this.initializeGame();
        }
    }

    public void changePlayer() {
        this.currentPlayer = this.currentPlayer == this.whitePlayer ? this.blackPlayer : this.whitePlayer;
    }

    public Piece getPiece(int i, int j) {
        return this.board[i][j];
    }

    public Player getCurrentPlayer() {
        return this.currentPlayer;
    }

    public Player getOtherPlayer() {
        return this.currentPlayer == this.whitePlayer ? this.blackPlayer : this.whitePlayer;
    }

    private Player getPlayerByColor(PlayerColor color) {
        return this.currentPlayer.getColor() == color ? this.currentPlayer : this.getOtherPlayer();
    }

    public Move getLastMove() {
        return this.lastMove;
    }

    public void setRealChessboard(boolean realChessboard) {
        this.realChessboard = realChessboard;
    }

    public boolean isRealChessboard() {
        return this.realChessboard;
    }

    public void removePiece(Piece piece) {
        this.getPlayerByColor(piece.getColor()).getPieces().remove(piece);
        this.board[piece.getPosition().x][piece.getPosition().y] = null;
    }

    public void addPieceBack(Piece piece, Position pos) {
        this.getPlayerByColor(piece.getColor()).getPieces().add(piece);
        this.board[pos.x][pos.y] = piece;
    }

    public void executeMove(Move move) {
        // potentially remove enemy piece
        if (move.getPieceToTake() != null) {
            Position posOfTakenPiece = move.getPieceToTake().getPosition();
            this.board[posOfTakenPiece.x][posOfTakenPiece.y] = null;
            this.getOtherPlayer().getPieces().remove(move.getPieceToTake());
        }

        // move piece to new position
        move.execute(this);
        this.lastMove = move;

        // update moves (without concern for king's safety)
        this.possibleMoves.clear();
        this.currentPlayer.getPieces().forEach(p -> p.updateMovesWithoutProtectingKing(this));
        // check is enemy king is now threatened
        this.getOtherPlayer().getKing().setIsChecked(
                this.isPosThreaten(this.getOtherPlayer().getKing().getPosition(), this.currentPlayer));

        // change player
        this.changePlayer();

        // calculate possible moves
        this.currentPlayer.getPieces().forEach(f -> f.updatePossibleMoves(this));

        // check conditions of victory
        if (this.currentPlayer.getAllPossibleMoves().isEmpty()) {
            if (this.currentPlayer.kingIsChecked()) {
                this.winner = this.getOtherPlayer();
            }
            else {
                this.draw = true;
            }
        }
    }

    public boolean simulateMove(Move move) {
        Move trulyLastMove = this.lastMove;

        // potentially remove enemy piece
        if (move.getPieceToTake() != null) {
            Position posOfTakenPiece = move.getPieceToTake().getPosition();
            this.board[posOfTakenPiece.x][posOfTakenPiece.y] = null;
            this.getOtherPlayer().getPieces().remove(move.getPieceToTake());
        }

        // move piece to new position
        move.execute(this);
        this.lastMove = move;

        // update moves (without concern for king's safety)
        this.getOtherPlayer().getPieces().forEach(p -> p.updateMovesWithoutProtectingKing(this));
        // check is current king is now threatened
        boolean isKingThreaten = this.isPosThreaten(this.currentPlayer.getKing().getPosition(), this.getOtherPlayer());

        // cancel
        this.lastMove = trulyLastMove;
        move.undo(this);

        // potentially return enemy piece
        if (move.getPieceToTake() != null) {
            Position posOfTakenPiece = move.getPieceToTake().getPosition();
            this.board[posOfTakenPiece.x][posOfTakenPiece.y] = move.getPieceToTake();
            this.getOtherPlayer().getPieces().add(move.getPieceToTake());
        }

        return isKingThreaten;
    }

    public boolean isPosThreaten(Position position, Player player) {
        return player.getPieces().stream().anyMatch(
                piece -> piece.getMovesWithoutProtectingKing().stream().anyMatch(m -> m.getNewPosition().equals(position))
        );
    }

    public Piece askForPromotedPiece() {
        return this.boardScreenController.getPromotedPiece();
    }

    /**
     *
     * @return null if no one is winner yet, or reference to winner if there is one already
     */
    public Player getWinner() {
        return this.winner;
    }

    /**
     *
     * @return true if game has ended in a draw, false otherwise
     */
    public boolean isDraw() {
        return this.draw;
    }


    private void addNewPiece(Piece piece, Player player, Position position) {
        this.board[position.x][position.y] = piece;
        piece.setPosition(position);
        player.getPieces().add(piece);
    }

    private void initializeGame() {
        // Create players
        this.whitePlayer = new Player(PlayerColor.WHITE);
        this.blackPlayer = new Player(PlayerColor.BLACK);

        // WHITES
        for (int i = 0; i < 8; i++) {
            addNewPiece(new Pawn(PlayerColor.WHITE), this.whitePlayer, new Position(i, 6));
        }
        addNewPiece(new Rook(PlayerColor.WHITE), this.whitePlayer, new Position(0, 7));
        addNewPiece(new Knight(PlayerColor.WHITE), this.whitePlayer, new Position(1, 7));
        addNewPiece(new Bishop(PlayerColor.WHITE), this.whitePlayer, new Position(2, 7));
        addNewPiece(new Queen(PlayerColor.WHITE), this.whitePlayer, new Position(3, 7));

        King whiteKing = new King(PlayerColor.WHITE);
        addNewPiece(whiteKing, this.whitePlayer, new Position(4, 7));
        this.whitePlayer.setKing(whiteKing);

        addNewPiece(new Bishop(PlayerColor.WHITE), this.whitePlayer, new Position(5, 7));
        addNewPiece(new Knight(PlayerColor.WHITE), this.whitePlayer, new Position(6, 7));
        addNewPiece(new Rook(PlayerColor.WHITE), this.whitePlayer, new Position(7, 7));

        // BLACKS
        for (int i = 0; i < 8; i++) {
            addNewPiece(new Pawn(PlayerColor.BLACK), this.blackPlayer, new Position(i, 1));
        }
        addNewPiece(new Rook(PlayerColor.BLACK), this.blackPlayer, new Position(0, 0));
        addNewPiece(new Knight(PlayerColor.BLACK), this.blackPlayer, new Position(1, 0));
        addNewPiece(new Bishop(PlayerColor.BLACK), this.blackPlayer, new Position(2, 0));
        addNewPiece(new Queen(PlayerColor.BLACK), this.blackPlayer, new Position(3, 0));

        King blackKing = new King(PlayerColor.BLACK);
        addNewPiece(blackKing, this.blackPlayer, new Position(4, 0));
        this.blackPlayer.setKing(blackKing);

        addNewPiece(new Bishop(PlayerColor.BLACK), this.blackPlayer, new Position(5, 0));
        addNewPiece(new Knight(PlayerColor.BLACK), this.blackPlayer, new Position(6, 0));
        addNewPiece(new Rook(PlayerColor.BLACK), this.blackPlayer, new Position(7, 0));

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
