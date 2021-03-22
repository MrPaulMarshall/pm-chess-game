package chessgame.controller;

import chessgame.model.pieces.*;
import chessgame.model.game.Game;
import chessgame.model.game.Player;
import chessgame.model.game.moves.Move;
import chessgame.model.properties.PlayerColor;
import chessgame.model.properties.Position;
import chessgame.presenter.BoardCell;
import chessgame.presenter.BoardScreenView;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.util.stream.Collectors;

public class BoardScreenController {

    private final Stage primaryStage;
    private final AppController appController;
    private BoardScreenView boardScreenView = null;

    private final BoardCell[][] boardCells;

    private Piece pieceChosen;

    private Game game;
    private boolean gameIsRunning;

    public BoardScreenController(AppController appController, Stage primaryStage) {
        this.appController = appController;
        this.primaryStage = primaryStage;

        this.boardCells = new BoardCell[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                boardCells[i][j] = new BoardCell(i, j, new StackPane(), this);
            }
        }
        this.pieceChosen = null;
    }

    public void initRootLayout() throws Exception {
        this.primaryStage.setTitle("Chess board");

        this.game = new Game(true, this);

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(BoardScreenController.class
                .getResource("/view/board_screen.fxml"));
        BorderPane rootLayout = loader.load();

        boardScreenView = loader.getController();
        boardScreenView.setBoardScreenAppController(this);
        boardScreenView.setBoardCells(boardCells);
        boardScreenView.setGame(this.game);
        this.gameIsRunning = true;

        boardScreenView.reloadBoardView();

        Scene scene = new Scene(rootLayout);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void boardCellOnClick(int i, int j) {
        if (!gameIsRunning) return;

        Move move;
        Piece piece;

        if (this.game.board[i][j] == this.pieceChosen) {
            this.pieceChosen = null;
            this.boardScreenView.refreshBackground();
        }
        else if (this.pieceChosen != null
                    && (move = this.pieceChosen.findMoveByTargetPosition(new Position(i, j))) != null) {
            this.executeMove(move);
        }
        else if ((piece = game.getPiece(i, j)) != null
                && piece.getColor() == game.getCurrentPlayer().getColor()) {
            choosePiece(piece);
        }
    }

    public void executeMove(Move move) {
        this.game.executeMove(move);
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

    public void choosePiece(Piece piece) {
        this.boardScreenView.refreshBackground();
        this.pieceChosen = piece;

        this.boardScreenView.setChosenPieceBackground(piece.getPosition());
        this.boardScreenView.setClickableBackgrounds(
                piece.getPossibleMoves().stream().map(Move::getNewPosition).collect(Collectors.toList()));
    }

    public Piece getPromotedPiece() {
        ChoosePromotionPieceController controller = new ChoosePromotionPieceController();
        PlayerColor color = game.getCurrentPlayer().getColor();
        String chosenPiece = controller.askForPromotionPiece(color);
        switch (chosenPiece) {
            case "queen":
                return new Queen(color);
            case "knight":
                return new Knight(color);
            case "rook":
                return new Rook(color);
            case "bishop":
                return new Bishop(color);
            default:
                throw new IllegalStateException("Piece chosen during promotion isn't valid");
        }
    }

    public void endGame(Player player) {
        this.gameIsRunning = false;
        this.boardScreenView.reloadBoardView();

        String result = player == null ? "THE GAME HAS ENDED IN A DRAW" : (player.getSignature() + " HAS WON, CONGRATULATIONS");

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
