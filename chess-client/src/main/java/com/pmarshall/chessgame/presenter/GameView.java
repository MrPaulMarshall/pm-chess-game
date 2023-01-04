package com.pmarshall.chessgame.presenter;

import com.pmarshall.chessgame.controller.GameController;
import com.pmarshall.chessgame.model.properties.PieceType;
import com.pmarshall.chessgame.model.properties.Color;
import com.pmarshall.chessgame.model.properties.Position;
import com.pmarshall.chessgame.model.service.Game;
import com.pmarshall.chessgame.services.ImageProvider;
import com.pmarshall.chessgame.services.LocalResourceImageProvider;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.*;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Collection;

/**
 * @author Paweł Marszał
 *
 * View class that displays main screen of the game (with board)
 */
public class GameView {

    /**
     * Reference to controller
     */
    private GameController gameController;

    /**
     * Board - arrays of cells (from GUI's perspective)
     */
    private ChessboardCell[][] chessboard;

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
        this.movesTextArea.setEditable(true);
        this.movesTextArea.appendText(game.currentPlayer().next() + ": " + (game.activeCheck() ? "+" : "") + game.lastMoveInNotation() + "\n");
        this.movesTextArea.setEditable(false);
    }

    /**
     * Sets all cells into their idle state
     */
    public void refreshBackground() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                this.chessboard[i][j].refreshBackground();
            }
        }

        if (game.activeCheck()) {
            markCheckedKingsField();
        }
    }

    private void markCheckedKingsField() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Pair<PieceType, Color> piece = gameController.getBoard()[i][j];
                if (piece != null && piece.getLeft() == PieceType.KING && piece.getRight() == game.currentPlayer()) {
                    this.chessboard[i][j].setCheckedBackground();
                    return;
                }
            }
        }
    }

    /**
     * Reloads whole information about state of the game
     */
    public void reloadBoardView() {
        this.refreshBackground();
        this.currentPlayerName.setText(game.currentPlayer().toString());
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Pair<PieceType, Color> piece = gameController.getBoard()[i][j];
                this.chessboard[i][j].setImage(
                        piece == null ? null : imageProvider.getImage(piece.getLeft(), piece.getRight()));
            }
        }
    }

    // Setters

    public void setBoardScreenAppController(GameController gameController) {
        this.gameController = gameController;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public void setBoardCells(ChessboardCell[][] board) {
        this.chessboard = board;

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                this.chessBoardGrid.add(this.chessboard[i][j].getPane(), i, j);
            }
        }
    }

    public void setChosenPieceBackground(Position position) {
        this.chessboard[position.x()][position.y()].setChosenBackground();
    }

    public void setClickableBackgrounds(Collection<Position> positions) {
        positions.forEach(pos -> this.chessboard[pos.x()][pos.y()].setClickableBackground());
    }

    @FXML
    public void handleSurrenderAction(ActionEvent ignored) {
        this.gameController.endGame(game.currentPlayer().next());
    }

    @FXML
    public void handleDrawAction(ActionEvent ignored) {
        this.gameController.endGame(null);
    }

}
