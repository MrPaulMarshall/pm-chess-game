package com.pmarshall.chessgame.controller;

import com.pmarshall.chessgame.model.properties.Color;
import com.pmarshall.chessgame.model.properties.PieceType;
import com.pmarshall.chessgame.model.properties.Position;
import com.pmarshall.chessgame.model.service.Game;
import com.pmarshall.chessgame.presenter.BoardCell;
import com.pmarshall.chessgame.presenter.BoardScreenView;
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
import org.apache.commons.lang3.tuple.Triple;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Paweł Marszał
 *
 * Represents controller of main view of the application
 */
public class BoardScreenController {

    private final Stage primaryStage;

    /**
     * References to model
     */
    private final Game game;

    private Map<Position, Set<Position>> currentLegalMoves;
    private Set<Pair<Position, Position>> promotions;

    public Pair<PieceType, Color>[][] getBoard() {
        return board;
    }

    private Pair<PieceType, Color>[][] board;

    private Position pieceChosen;
    private boolean gameIsRunning;

    /**
     * References to view
     */
    private final BoardCell[][] boardCells;
    private BoardScreenView boardScreenView = null;

    public BoardScreenController(Stage primaryStage, Game game) {
        this.primaryStage = primaryStage;
        this.gameIsRunning = false;
        this.game = game;

        this.boardCells = new BoardCell[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                boardCells[i][j] = new BoardCell(i, j, this);
            }
        }
    }

    /**
     * Initializes game and displays view
     * @throws Exception if anything goes wrong
     */
    public void initRootLayout() throws Exception {
        this.primaryStage.setTitle("Chess board");

        this.pieceChosen = null;
        this.gameIsRunning = true;
        reloadBoard();

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(BoardScreenController.class
                .getResource("/view/board_screen.fxml"));
        BorderPane rootLayout = loader.load();

        boardScreenView = loader.getController();
        boardScreenView.setBoardScreenAppController(this);
        boardScreenView.setBoardCells(boardCells);
        boardScreenView.setGame(this.game);

        boardScreenView.reloadBoardView();

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
        if (clickedCell.equals(this.pieceChosen)) {
            this.pieceChosen = null;
            this.boardScreenView.refreshBackground();
        }
        // if player chosen valid move, execute it
        else if (this.pieceChosen != null
                && this.currentLegalMoves.getOrDefault(this.pieceChosen, Set.of()).contains(clickedCell)) {
            this.executeMove(this.pieceChosen, clickedCell);
        }
        // if player clicked on another of his pieces, mark it
        else if (board[i][j] != null && board[i][j].getRight() == game.currentPlayer()) {
            choosePiece(clickedCell);
        }
    }

    /**
     * Executes move in the model, calls Promotion dialog window if needed,
     * checks end-game conditions, and refreshes the board
     */
    public void executeMove(Position from, Position to) {
        boolean successfulMove;
        if (this.promotions.contains(Pair.of(from, to))) {
            successfulMove = this.game.executeMove(from, to, getPromotedPiece());
        } else {
            successfulMove = this.game.executeMove(from, to);
        }

        if (!successfulMove)
            return;

        this.reloadBoard();

        this.boardScreenView.printLastMove();
        this.boardScreenView.reloadBoardView();

        // check win conditions
        Pair<Color, String> gameOutcome = game.outcome();
        if (gameOutcome != null) {
            // TODO: use the message as well
            this.endGame(gameOutcome.getLeft());
        }

        // game is still running
        this.pieceChosen = null;
    }

    /**
     * Marks piece at specified position, so the player can move it
     */
    public void choosePiece(Position pieceChosen) {
        this.boardScreenView.refreshBackground();
        this.pieceChosen = pieceChosen;

        this.boardScreenView.setChosenPieceBackground(pieceChosen);
        this.boardScreenView.setClickableBackgrounds(this.currentLegalMoves.getOrDefault(pieceChosen, Set.of()));
    }

    private void reloadBoard() {
        this.board = game.getBoardWithPieces();

        Collection<Triple<Position, Position, Boolean>> moves = game.legalMoves();
        this.currentLegalMoves = moves.stream().collect(
                Collectors.groupingBy(Triple::getLeft, Collectors.mapping(Triple::getMiddle, Collectors.toSet())));
        this.promotions = moves.stream().filter(Triple::getRight)
                .map(triple -> Pair.of(triple.getLeft(), triple.getMiddle())).collect(Collectors.toUnmodifiableSet());
    }

    /**
     * Created dialog window with pieces that player can choose during promotion
     * @return piece chosen by player
     */
    public PieceType getPromotedPiece() {
        ChoosePromotionPieceController controller = new ChoosePromotionPieceController();
        return controller.askForPromotionPiece(game.currentPlayer());
    }

    /**
     * Disables board, creates dialog with result, and closes the program
     * @param winner winner of the game (or null, if draw)
     */
    public void endGame(Color winner) {
        // this disables 'Draw' and 'Surrender' buttons after end of the game, when board is still visible
        if (!this.gameIsRunning) return;

        this.gameIsRunning = false;
        this.boardScreenView.reloadBoardView();

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

        this.primaryStage.close();
    }
}
