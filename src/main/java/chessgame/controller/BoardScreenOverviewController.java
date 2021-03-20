package chessgame.controller;

import chessgame.presenter.BoardCell;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.*;

public class BoardScreenOverviewController {

    private BoardScreenAppController boardScreenAppController;
    private BoardCell[][] boardCells;

    @FXML
    private GridPane chessBoardGrid;

    @FXML
    public void initialize() throws Exception {
    }

    public void setBoardScreenAppController(BoardScreenAppController boardScreenAppController) {
        this.boardScreenAppController = boardScreenAppController;
    }

    public void setBoardCells(BoardCell[][] boardCells) {
        this.boardCells = boardCells;

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                chessBoardGrid.add(this.boardCells[i][j].getPane(), i, j);
            }
        }
    }

    @FXML
    public void handleSurrenderAction(ActionEvent e) {

    }

    @FXML
    public void handleExitAction(ActionEvent e) {

        System.exit(0);
    }

}
