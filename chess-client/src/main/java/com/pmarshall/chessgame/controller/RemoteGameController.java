package com.pmarshall.chessgame.controller;

import com.pmarshall.chessgame.model.dto.Piece;
import com.pmarshall.chessgame.model.properties.Color;
import com.pmarshall.chessgame.model.properties.PieceType;
import com.pmarshall.chessgame.model.properties.Position;
import com.pmarshall.chessgame.remote.RemoteGameProxy;
import com.pmarshall.chessgame.services.ImageProvider;
import com.pmarshall.chessgame.services.LocalResourceImageProvider;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.util.Collection;

/**
 * @author Paweł Marszał
 *
 * Controller of game view in remote mode
 */
public class RemoteGameController {

    @FXML
    // Label that displays which player currently makes move
    private Label currentPlayerName;

    @FXML
    // Pane that contains whole board - it contains smaller panes: cells
    private GridPane chessBoardGrid;

    @FXML
    // Area when history of moves is displayed
    private TextArea movesTextArea;


    private Stage primaryStage;

    private RemoteGameProxy game;

    private Position pieceChosen;
    private boolean gameIsRunning;

    private ChessboardCell[][] chessboard;

    private int moveCounter = 1;

    private Position checkedKing;
    private final ImageProvider imageProvider = new LocalResourceImageProvider();

    /**
     * Initializes game and displays view
     * @throws IOException if the view cannot be loaded
     */
    public static void initRootLayout(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(RemoteGameController.class.getResource("/view/remote_game_screen.fxml"));

        BorderPane rootLayout = loader.load();
        Scene scene = new Scene(rootLayout);

        RemoteGameController controller = loader.getController();
        RemoteGameProxy game = RemoteGameProxy.connectToServer(controller);
        controller.injectDependencies(primaryStage, game);

        primaryStage.setTitle("Chess board");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void injectDependencies(Stage primaryStage, RemoteGameProxy game) {
        this.primaryStage = primaryStage;
        this.game = game;

        // TODO: init flags
        pieceChosen = null;
        gameIsRunning = true;

        this.chessboard = new ChessboardCell[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                final int it = i, jt = j;
                // TODO: what needs to be switched when we play as black?
                chessboard[i][j] = new ChessboardCell((i+j) % 2 == 0, e -> boardCellOnClick(it, jt));
                chessBoardGrid.add(chessboard[i][j].getPane(), i, j);
            }
        }

        refreshAfterMove(game.currentPlayer(), game.getBoardWithPieces());
    }

    @FXML
    public void initialize() {
    }

    @FXML
    public void handleSurrenderAction(ActionEvent ignored) throws InterruptedException {
        game.surrender();
    }

    @FXML
    public void handleDrawAction(ActionEvent ignored) throws InterruptedException {
        game.proposeDraw();
    }

    public void showDrawRequestedWindow() {
        // TODO: disable interacting with the board
        DrawRequestController controller = new DrawRequestController(this);
        controller.displayWindow();
    }

    /**
     * Disables board, creates dialog with result, and closes the program
     * @param winner winner of the game (or null, if draw)
     */
    public void endGame(Color winner) {
        // this disables 'Draw' and 'Surrender' buttons after end of the game, when board is still visible
        if (!gameIsRunning) return;

        gameIsRunning = false;
        refreshAfterMove(game.currentPlayer(), game.getBoardWithPieces());

        String result = winner == null ? "THE GAME HAS ENDED IN A DRAW"
                : (winner.toString().toUpperCase() + " HAS WON, CONGRATULATIONS");

        GameEndedController gameEndedController = new GameEndedController(primaryStage);
        gameEndedController.initRootLayout(result);

        primaryStage.close();
    }

    // TODO: rename those functions
    public void refreshBoard() {
        refreshAfterMove(game.currentPlayer(), game.getBoardWithPieces(), game.activeCheck(), game.lastMoveInNotation());

        // check win conditions
        Pair<Color, String> gameOutcome = game.outcome();
        if (gameOutcome != null) {
            // TODO: use the message as well
            endGame(gameOutcome.getLeft());
        }

        // game is still running
        pieceChosen = null;
    }

    /**
     * Reacts to player's click on the cell of the board
     */
    private void boardCellOnClick(int i, int j) {
        // if game has ended, board is disabled
        if (!gameIsRunning)
            return;

        Position clickedCell = new Position(i, j);

        // if player click on the chosen piece again, unmark it
        if (clickedCell.equals(pieceChosen)) {
            pieceChosen = null;
            repaintBackground();
            return;
        }

        // if player chosen valid move, execute it
        if (pieceChosen != null && game.isMoveLegal(pieceChosen, clickedCell)) {
            executeMove(pieceChosen, clickedCell);
            return;
        }

        // if player clicked on another of his pieces, mark it
        Piece piece = game.getPiece(clickedCell);
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

        refreshBoard();
    }

    /**
     * Created dialog window with pieces that player can choose during promotion
     * @return piece chosen by player
     */
    private PieceType getPromotedPiece() {
        // TODO: disable interacting with the board
        PromotionController controller = new PromotionController();
        return controller.askForPromotionPiece(game.currentPlayer());
    }

    private void refreshAfterMove(Color player, Piece[][] board, boolean check, String notation) {
        checkedKing = check ? findCheckedKing(player, board) : null;

        appendMoveToLedger(player, notation);
        refreshAfterMove(player.next(), board);
    }

    /**
     * Reloads whole information about state of the game
     */
    private void refreshAfterMove(Color player, Piece[][] board) {
        repaintBackground();
        currentPlayerName.setText(player.toString());
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece piece = board[i][j];
                Image image = piece == null ? null : imageProvider.getImage(piece.piece(), piece.color());
                chessboard[i][j].setImage(image);
            }
        }
    }

    /**
     * Sets all cells into their idle state, but shows the active check
     */
    private void repaintBackground() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                chessboard[i][j].refreshBackground();
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
        chessboard[picked.x()][picked.y()].setChosenBackground();
        for (Position target : legalTargets) {
            chessboard[target.x()][target.y()].setClickableBackground();
        }
    }

    /**
     * Prints last move into text area
     */
    private void appendMoveToLedger(Color player, String notation) {
        movesTextArea.setEditable(true);
        if (player == Color.WHITE) {
            movesTextArea.appendText(moveCounter + ". " + notation);
        } else {
            movesTextArea.appendText(" " + notation + "\n");
            moveCounter++;
        }
        movesTextArea.setEditable(false);
    }

    private Position findCheckedKing(Color color, Piece[][] board) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece piece = board[i][j];
                if (piece != null && piece.piece() == PieceType.KING && piece.color() == color) {
                    return new Position(i, j);
                }
            }
        }
        throw new IllegalStateException("King must be on the board");
    }

    private void markCheckedKingsField() {
        chessboard[checkedKing.x()][checkedKing.y()].setCheckedBackground();
    }
}
