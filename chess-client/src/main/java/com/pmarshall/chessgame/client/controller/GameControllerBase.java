package com.pmarshall.chessgame.client.controller;

import com.pmarshall.chessgame.model.dto.LegalMove;
import com.pmarshall.chessgame.model.dto.Piece;
import com.pmarshall.chessgame.model.properties.Color;
import com.pmarshall.chessgame.model.properties.PieceType;
import com.pmarshall.chessgame.model.properties.Position;
import com.pmarshall.chessgame.model.service.Game;
import com.pmarshall.chessgame.client.remote.RemoteGameProxy;
import com.pmarshall.chessgame.client.services.ImageProvider;
import com.pmarshall.chessgame.client.services.LocalResourceImageProvider;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Collection;

public abstract class GameControllerBase {

    @FXML
    // Label that displays which player currently makes move
    protected Label currentPlayerName;

    @FXML
    // Pane that contains whole board - it contains smaller panes: cells
    protected GridPane chessBoardGrid;

    @FXML
    // Area when history of moves is displayed
    protected TextArea movesTextArea;

    protected Stage primaryStage;

    protected Game game;

    protected ChessboardCell[][] chessboard;

    protected int moveCounter = 1;

    protected Position pieceChosen;

    protected volatile boolean gameEnded;

    protected Position checkedKing;

    protected final ImageProvider imageProvider = new LocalResourceImageProvider();

    @FXML
    public void initialize() {
    }

    protected void createBoardGrid(boolean forward) {
        this.chessboard = new ChessboardCell[8][8];
        for (int rank = 0; rank < 8; rank++) {
            for (int file = 0; file < 8; file++) {
                final int constRank = rank, constFile = file;
                chessboard[rank][file] = new ChessboardCell(
                        (rank+file) % 2 == 0, e -> boardCellOnClick(constRank, constFile));
                chessBoardGrid.add(chessboard[constRank][constFile].getPane(), file, forward ? rank : 7-rank);
            }
        }
    }

    /**
     * Disables board, creates dialog with result, and closes the program
     * @param winner winner of the game (or null, if draw)
     */
    public void endGame(Color winner) {
        // this disables 'Draw' and 'Surrender' buttons after end of the game, when board is still visible
        if (gameEnded) return;

        gameEnded = true;
        refreshBoard(game.currentPlayer(), game.getBoardWithPieces());

        String result = winner == null ? "THE GAME HAS ENDED IN A DRAW"
                : (winner.toString().toUpperCase() + " HAS WON, CONGRATULATIONS");

        GameEndedController gameEndedController = new GameEndedController(primaryStage);
        gameEndedController.initRootLayout(result);
    }

    /**
     * Reacts to player's click on the cell of the board
     */
    protected void boardCellOnClick(int rank, int file) {
        // if game has ended, board is disabled
        if (gameEnded)
            return;

        Position clickedCell = new Position(rank, file);

        // if player click on the chosen piece again, unmark it
        if (clickedCell.equals(pieceChosen)) {
            pieceChosen = null;
            repaintBackground();
            return;
        }

        // if player chosen valid move, execute it
        if (this.pieceChosen != null && game.isMoveLegal(pieceChosen, clickedCell)) {
            executeMove(pieceChosen, clickedCell);
            return;
        }

        // if player clicked on another of his pieces, mark it
        Piece piece = game.getPiece(clickedCell);

        // TODO: refactor me
        if (piece != null && game instanceof RemoteGameProxy proxy && piece.color() != proxy.localPlayer()) {
            return;
        }

        if (piece != null && piece.color() == game.currentPlayer()) {
            pieceChosen = clickedCell;
            choosePiece(pieceChosen, game.legalMovesFrom(pieceChosen));
        }
    }

    /**
     * Executes move in the model, calls Promotion dialog window if needed,
     * checks end-game conditions, and refreshes the board
     */
    private void executeMove(Position from, Position to) {
        boolean successfulMove;
        if (game.isPromotionRequired(from, to)) {
            successfulMove = game.executeMove(from, to, getPromotedPiece());
        } else {
            successfulMove = game.executeMove(from, to);
        }

        if (!successfulMove)
            return;

        refreshStageAfterMove(game.currentPlayer().next(), game.lastMove(), game.getBoardWithPieces(), game.outcome());
    }

    /**
     * Created dialog window with pieces that player can choose during promotion
     * @return piece chosen by player
     */
    private PieceType getPromotedPiece() {
        PromotionController controller = new PromotionController(primaryStage);
        return controller.askForPromotionPiece(game.currentPlayer());
    }

    public void refreshStageAfterMove(Color player, LegalMove move, Piece[][] board, Pair<Color, String> gameOutcome) {
        pieceChosen = null;
        checkedKing = move.check() ? findCheckedKing(player.next(), board) : null;

        appendMoveToLedger(player, move.notation());
        refreshBoard(player.next(), board);

        // check win conditions
        if (gameOutcome != null) {
            // TODO: use the message as well
            endGame(gameOutcome.getLeft());
        }
    }

    /**
     * Reloads whole information about state of the game
     */
    protected void refreshBoard(Color player, Piece[][] board) {
        repaintBackground();
        currentPlayerName.setText(player.toString());
        for (int rank = 0; rank < 8; rank++) {
            for (int file = 0; file < 8; file++) {
                Piece piece = board[rank][file];
                Image image = piece == null ? null : imageProvider.getImage(piece.piece(), piece.color());
                chessboard[rank][file].setImage(image);
            }
        }
    }

    /**
     * Sets all cells into their idle state, but shows the active check
     */
    private void repaintBackground() {
        for (int rank = 0; rank < 8; rank++) {
            for (int file = 0; file < 8; file++) {
                this.chessboard[rank][file].refreshBackground();
            }
        }

        if (checkedKing != null) {
            markCheckedKingsField();
        }
    }

    /**
     * Refreshes the board coloring, then marks chosen piece and possible destinations
     */
    private void choosePiece(Position picked, Collection<Position> legalTargets) {
        repaintBackground();
        chessboard[picked.rank()][picked.file()].setChosenBackground();
        for (Position target : legalTargets) {
            chessboard[target.rank()][target.file()].setClickableBackground();
        }
    }

    /**
     * Prints last move into text area
     */
    private void appendMoveToLedger(Color player, String notation) {
        this.movesTextArea.setEditable(true);
        if (player == Color.WHITE) {
            this.movesTextArea.appendText(moveCounter + ". " + notation);
        } else {
            this.movesTextArea.appendText(" " + notation + "\n");
            moveCounter++;
        }
        this.movesTextArea.setEditable(false);
    }

    private Position findCheckedKing(Color color, Piece[][] board) {
        for (int rank = 0; rank < 8; rank++) {
            for (int file = 0; file < 8; file++) {
                Piece piece = board[rank][file];
                if (piece != null && piece.piece() == PieceType.KING && piece.color() == color) {
                    return new Position(rank, file);
                }
            }
        }
        throw new IllegalStateException("King must be on the board");
    }

    private void markCheckedKingsField() {
        this.chessboard[checkedKing.rank()][checkedKing.file()].setCheckedBackground();
    }

}
