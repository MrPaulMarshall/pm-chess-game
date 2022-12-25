package com.pmarshall.chessgame.controller;

import com.pmarshall.chessgame.model.game.PiecePromotionSource;
import com.pmarshall.chessgame.model.game.Game;
import com.pmarshall.chessgame.model.game.Player;
import com.pmarshall.chessgame.model.moves.Move;
import com.pmarshall.chessgame.model.pieces.*;
import com.pmarshall.chessgame.model.properties.Color;
import com.pmarshall.chessgame.model.properties.Position;
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

import java.util.stream.Collectors;

/**
 * @author Paweł Marszał
 *
 * Represents controller of main view of the application
 */
public class BoardScreenController implements PiecePromotionSource {

    private final Stage primaryStage;

    /**
     * References to model
     */
    private Game game;
    private Piece pieceChosen;
    private boolean gameIsRunning;

    /**
     * References to view
     */
    private final BoardCell[][] boardCells;
    private BoardScreenView boardScreenView = null;

    public BoardScreenController(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.gameIsRunning = false;

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

        this.game = new Game(this);
        this.pieceChosen = null;
        this.gameIsRunning = true;

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
        if (!gameIsRunning) return;

        Move move;
        Piece piece;

        // if player click on the chosen piece again, unmark it
        if (this.game.board[i][j] == this.pieceChosen) {
            this.pieceChosen = null;
            this.boardScreenView.refreshBackground();
        }
        // if player chosen valid move, execute it
        else if (this.pieceChosen != null
                    && (move = this.pieceChosen.findMoveByTargetPosition(new Position(i, j))) != null) {
            this.executeMove(move);
        }
        // if player clicked on another of his pieces, mark it
        else if ((piece = game.getPiece(i, j)) != null
                && piece.getColor() == game.getCurrentPlayer().getColor()) {
            choosePiece(piece);
        }
    }

    /**
     * Executes move in the model, checks end-game conditions, and refreshes the board
     * @param move move picked by player
     */
    public void executeMove(Move move) {
        this.game.executeMove(move);
        this.boardScreenView.printLastMove();
        this.boardScreenView.reloadBoardView();

        // check win condition etc.
        if (game.getWinner() != null) {
            this.endGame(game.getWinner());
        }
        if (game.isDraw()) {
            this.endGame(null);
        }

        // game still lasts
        this.pieceChosen = null;
    }

    /**
     * Marks piece, so the player can move it
     * @param piece picked by player
     */
    public void choosePiece(Piece piece) {
        this.boardScreenView.refreshBackground();
        this.pieceChosen = piece;

        this.boardScreenView.setChosenPieceBackground(piece.getPosition());
        this.boardScreenView.setClickableBackgrounds(
                piece.getPossibleMoves().stream().map(Move::getNewPosition).collect(Collectors.toList()));
    }

    /**
     * Created dialog window with pieces that player can choose during promotion
     * @return piece chosen by player
     */
    public Piece getPromotedPiece() {
        ChoosePromotionPieceController controller = new ChoosePromotionPieceController();
        Color color = game.getCurrentPlayer().getColor();
        String chosenPiece = controller.askForPromotionPiece(color);
        return switch (chosenPiece) {
            case "queen" -> new Queen(color);
            case "knight" -> new Knight(color);
            case "rook" -> new Rook(color);
            case "bishop" -> new Bishop(color);
            default -> throw new IllegalStateException("Piece chosen during promotion isn't valid");
        };
    }

    /**
     * Disables board, creates dialog with result, and closes the program
     * @param player winner of the game (or null, if draw)
     */
    public void endGame(Player player) {
        // this disables 'Draw' and 'Surrender' buttons after end of the game, when board is still visible
        if (!this.gameIsRunning) return;

        this.gameIsRunning = false;
        this.boardScreenView.reloadBoardView();

        String result = player == null ? "THE GAME HAS ENDED IN A DRAW" : (player + " HAS WON, CONGRATULATIONS");

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
