package chessgame.controller;

import chessgame.model.figures.Figure;
import chessgame.model.game.Game;
import chessgame.model.game.moves.Move;
import chessgame.model.properties.Position;
import chessgame.presenter.BoardCell;
import chessgame.presenter.BoardScreenView;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.stream.Collectors;

public class BoardScreenController {

    private final Stage primaryStage;
    private final AppController appController;
    private BoardScreenView boardScreenView = null;

    private final BoardCell[][] boardCells;

    private Figure figureChosen;

    private Game game;

    public BoardScreenController(AppController appController, Stage primaryStage) {
        this.appController = appController;
        this.primaryStage = primaryStage;

        this.boardCells = new BoardCell[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                boardCells[i][j] = new BoardCell(i, j, new StackPane(), this);
            }
        }
        this.figureChosen = null;
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

        boardScreenView.reloadBoardView();

        Scene scene = new Scene(rootLayout);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void boardCellOnClick(int i, int j) {
        Move move;
        Figure figure;

        if (this.game.board[i][j] == this.figureChosen) {
            this.figureChosen = null;
            this.boardScreenView.refreshBackground();
        }
        else if (this.figureChosen != null
                    && (move = this.figureChosen.findMoveByTargetPosition(new Position(i, j))) != null) {
            this.executeMove(move);
        }
        else if ((figure = game.getFigure(i, j)) != null
                && figure.getColor() == game.getCurrentPlayer().getColor()) {
            chooseFigure(figure);
        }
    }

    public void executeMove(Move move) {
        this.game.executeMove(move);
        this.boardScreenView.reloadBoardView();

        // check win condition etc.
        if (game.getWinner() != null) {
            // TODO: end game
            System.out.println("THE END - Game is won");
        }
        if (game.isDraw()) {
            // TODO: end game
            System.out.println("THE END - Game has ended in a draw");
        }

        // game still lasts
        this.figureChosen = null;
    }

    public void chooseFigure(Figure figure) {
        this.boardScreenView.refreshBackground();
        this.figureChosen = figure;

        this.boardScreenView.setChosenPieceBackground(figure.getPosition());
        this.boardScreenView.setClickableBackgrounds(
                figure.getPossibleMoves().stream().map(Move::getNewPosition).collect(Collectors.toList()));
    }

    public Figure getPromotedPiece() {
        return null;
    }
}
