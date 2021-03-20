package chessgame.controller;

import chessgame.presenter.BoardCell;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class BoardScreenAppController {

    private final Stage primaryStage;
    private final AppController appController;

    private final BoardCell[][] boardCells;

    public BoardScreenAppController(AppController appController, Stage primaryStage) {
        this.appController = appController;
        this.primaryStage = primaryStage;

        this.boardCells = new BoardCell[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                boardCells[i][j] = new BoardCell(i, j, new StackPane(), this);
            }
        }
    }

    public void initRootLayout() throws Exception {
        this.primaryStage.setTitle("Chess board");

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(BoardScreenAppController.class
                .getResource("/view/board_screen.fxml"));
        BorderPane rootLayout = loader.load();

        BoardScreenOverviewController controller = loader.getController();
        controller.setBoardScreenAppController(this);
        controller.setBoardCells(boardCells);

        Scene scene = new Scene(rootLayout);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void boardCellOnClick(int i, int j) {
        // TODO
    }
}
