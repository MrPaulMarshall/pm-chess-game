package chessgame.presenter;

import chessgame.controller.BoardScreenController;
import chessgame.model.game.Game;
import chessgame.model.properties.Position;
import chessgame.services.ImageProvider;
import chessgame.services.LocalResourceImageProvider;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.*;

import java.util.List;

/**
 * @author Paweł Marszał
 *
 * View class that displays main screen of the game (with board)
 */
public class BoardScreenView {

    /**
     * Reference to controller
     */
    private BoardScreenController boardScreenController;

    /**
     * Board - arrays of cells (from GUI's perspective)
     */
    private BoardCell[][] boardCells;

    /**
     * Reference to Game object, to read data to display from the model
     */
    private Game game;

    /**
     * Source of the images to display the pieces
     */
    private final ImageProvider imageProvider = new LocalResourceImageProvider();

    /**
     * Label that displays which player currently makes move
     */
    @FXML
    private Label currentPlayerName;

    /**
     * Pane that contains whole board - it contains smaller panes: cells
     */
    @FXML
    private GridPane chessBoardGrid;

    /**
     * Area when history of moves is displayed
     */
    @FXML
    private TextArea movesTextArea;

    @FXML
    public void initialize() {
    }

    /**
     * Prints last move into text area
     */
    public void printLastMove() {
        this.movesTextArea.appendText(
                game.getLastMove().getPieceToMove().getColor().toString() + ": "
                        + (game.getCurrentPlayer().isKingChecked() ? "+" : "")
                        + game.getLastMove().toString() + "\n"
        );
    }

    /**
     * Sets all cells into their idle state
     */
    public void refreshBackground() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                this.boardCells[i][j].refreshBackground();
            }
        }
    }

    /**
     * Reloads whole information about state of the game
     */
    public void reloadBoardView() {
        this.refreshBackground();
        this.currentPlayerName.setText(game.getCurrentPlayer().toString());
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                this.boardCells[i][j].setImage(
                        game.getPiece(i, j) == null ? null : imageProvider.getImage(game.getPiece(i, j)));
            }
        }
    }

    // Setters

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

    public void setChosenPieceBackground(Position position) {
        this.boardCells[position.x][position.y].setChosenBackground();
    }

    public void setClickableBackgrounds(List<Position> positions) {
        positions.forEach(pos -> this.boardCells[pos.x][pos.y].setClickableBackground());
    }

    @FXML
    public void handleSurrenderAction(ActionEvent e) {
        this.boardScreenController.endGame(this.game.getOtherPlayer());
    }

    @FXML
    public void handleDrawAction(ActionEvent e) {
        this.boardScreenController.endGame(null);
    }

}
