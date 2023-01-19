package com.pmarshall.chessgame.controller;

import com.pmarshall.chessgame.model.dto.Piece;
import com.pmarshall.chessgame.model.properties.Color;
import com.pmarshall.chessgame.model.properties.PieceType;
import com.pmarshall.chessgame.model.properties.Position;
import com.pmarshall.chessgame.model.service.Game;
import com.pmarshall.chessgame.presenter.GameView;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import org.apache.commons.lang3.tuple.Pair;

/**
 * @author Paweł Marszał
 *
 * Represents controller of main view of the application
 */
public class GameController {

    private final Stage primaryStage;

    private final Game game;

    private Position pieceChosen;
    private boolean gameIsRunning;

    private GameView gameView = null;

    private final boolean remoteMode;

    public GameController(Stage primaryStage, Game game, boolean remoteMode) {
        this.primaryStage = primaryStage;
        this.gameIsRunning = false;
        this.game = game;
        this.remoteMode = remoteMode;
    }

    /**
     * Initializes game and displays view
     * @throws Exception if anything goes wrong
     */
    public void initRootLayout() throws Exception {
        primaryStage.setTitle("Chess board");

        pieceChosen = null;
        gameIsRunning = true;

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(GameController.class
                .getResource("/view/board_screen.fxml"));
        BorderPane rootLayout = loader.load();

        gameView = loader.getController();
        gameView.setController(this);

        gameView.refreshAfterMove(game.currentPlayer(), game.getBoardWithPieces());

        Scene scene = new Scene(rootLayout);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Reacts to player's click on the cell of the board
     * @param i column coordinate
     * @param j row coordinate
     */
    public void boardCellOnClick(int i, int j) {
        // if game has ended, board is disabled
        if (!gameIsRunning)
            return;

        Position clickedCell = new Position(i, j);

        // if player click on the chosen piece again, unmark it
        if (clickedCell.equals(pieceChosen)) {
            pieceChosen = null;
            gameView.repaintBackground();
            return;
        }

        // if player chosen valid move, execute it
        if (this.pieceChosen != null && game.isMoveLegal(pieceChosen, clickedCell)) {
            executeMove(pieceChosen, clickedCell);
            return;
        }

        // if player clicked on another of his pieces, mark it
        Piece piece = game.getPiece(clickedCell);
        if (piece != null && piece.color() == game.currentPlayer()) {
            pieceChosen = clickedCell;
            gameView.choosePiece(pieceChosen, game.legalMovesFrom(pieceChosen));
        }
    }

    public void surrenderButtonOnClick() {
        if (remoteMode) {
            game.surrender(); // notify server
        } else {
            endGame(game.currentPlayer().next());
        }
    }

    public void drawButtonOnClick() {
        if (remoteMode) {
            game.draw();
        } else {
            endGame(null);
        }
    }

    /**
     * Executes move in the model, calls Promotion dialog window if needed,
     * checks end-game conditions, and refreshes the board
     */
    public void executeMove(Position from, Position to) {
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

    public void refreshBoard() {
        gameView.refreshAfterMove(
                game.currentPlayer(), game.getBoardWithPieces(), game.activeCheck(), game.lastMoveInNotation());

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
     * Created dialog window with pieces that player can choose during promotion
     * @return piece chosen by player
     */
    private PieceType getPromotedPiece() {
        // TODO: disable interacting with the board
        PromotionController controller = new PromotionController();
        return controller.askForPromotionPiece(game.currentPlayer());
    }

    public void showDrawRequestedWindow() {
        // TODO: disable interacting with the board
        DrawRequestController controller = new DrawRequestController(this);
        controller.displayWindow();
    }

    public void acceptDraw() {
        // TODO
    }

    public void rejectDraw() {
        // TODO
    }

    /**
     * Disables board, creates dialog with result, and closes the program
     * @param winner winner of the game (or null, if draw)
     */
    public void endGame(Color winner) {
        // this disables 'Draw' and 'Surrender' buttons after end of the game, when board is still visible
        if (!gameIsRunning) return;

        gameIsRunning = false;
        gameView.refreshAfterMove(game.currentPlayer(), game.getBoardWithPieces());

        String result = winner == null ? "THE GAME HAS ENDED IN A DRAW"
                : (winner.toString().toUpperCase() + " HAS WON, CONGRATULATIONS");

        Stage endGameStage = new Stage();
        endGameStage.setTitle("End game dialog");

        HBox buttonHBox = new HBox();
        ToggleButton button = new ToggleButton("Exit");
        button.setOnAction((event) -> {    // lambda expression
            endGameStage.close();
        });
        buttonHBox.getChildren().add(button);
        buttonHBox.setAlignment(Pos.CENTER_RIGHT);

        Label resultLabel = new Label(result);
        resultLabel.setWrapText(true);
        resultLabel.setTextAlignment(TextAlignment.CENTER);
        resultLabel.setTranslateX(10);
        resultLabel.setFont(Font.font(30));
        resultLabel.setMaxWidth(380);

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));
        root.setCenter(resultLabel);
        root.setBottom(buttonHBox);

        Scene scene = new Scene(root);
        endGameStage.setScene(scene);
        endGameStage.setWidth(400);
        endGameStage.setHeight(250);
        endGameStage.showAndWait();

        primaryStage.close();
    }
}
