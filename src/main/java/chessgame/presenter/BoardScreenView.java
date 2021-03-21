package chessgame.presenter;

import chessgame.controller.BoardScreenController;
import chessgame.model.game.Game;
import chessgame.model.properties.Position;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

import java.util.List;

public class BoardScreenView {

    private BoardScreenController boardScreenController;
    private BoardCell[][] boardCells;
    private Game game;

    @FXML
    private Label currentPlayerName;

    @FXML
    private GridPane chessBoardGrid;

    @FXML
    public void initialize() throws Exception {
    }

    public void setBoardScreenAppController(BoardScreenController boardScreenController) {
        this.boardScreenController = boardScreenController;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public void setBoardCells(BoardCell[][] boardCells) {
        this.boardCells = boardCells;

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                this.chessBoardGrid.add(this.boardCells[i][j].getPane(), i, j);
            }
        }
    }

    public void refreshBackground() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                this.boardCells[i][j].refreshBackground();
            }
        }
    }

    public void reloadBoardView() {
        this.refreshBackground();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                this.boardCells[i][j].setImage(game.getFigure(i, j) == null ? null : game.getFigure(i, j).getImage());
            }
        }
    }

    public void setChosenPieceBackground(Position position) {
        this.boardCells[position.x][position.y].setChosenBackground();
    }

    public void setClickableBackgrounds(List<Position> positions) {
        positions.forEach(pos -> this.boardCells[pos.x][pos.y].setClickableBackground());
    }

    @FXML
    public void handleSurrenderAction(ActionEvent e) {

    }

    @FXML
    public void handleExitAction(ActionEvent e) {
        System.exit(0);
    }

}
