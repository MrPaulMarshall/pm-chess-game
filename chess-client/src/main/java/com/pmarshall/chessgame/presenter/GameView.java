package com.pmarshall.chessgame.presenter;

import com.pmarshall.chessgame.controller.GameController;
import com.pmarshall.chessgame.model.dto.Piece;
import com.pmarshall.chessgame.model.properties.PieceType;
import com.pmarshall.chessgame.model.properties.Color;
import com.pmarshall.chessgame.model.properties.Position;
import com.pmarshall.chessgame.services.ImageProvider;
import com.pmarshall.chessgame.services.LocalResourceImageProvider;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.*;

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
     * Counter of moves made during the game
     */
    private int moveCounter = 1;

    /**
     * Position of the checked king
     */
    private Position checkedKing;

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

    public void refreshAfterMove(Color player, Piece[][] board, boolean check, String notation) {
        checkedKing = check ? findCheckedKing(player, board) : null;

        appendMoveToLedger(player, notation);
        refreshAfterMove(player.next(), board);
    }

    /**
     * Reloads whole information about state of the game
     */
    public void refreshAfterMove(Color player, Piece[][] board) {
        this.repaintBackground();
        this.currentPlayerName.setText(player.toString());
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece piece = board[i][j];
                this.chessboard[i][j].setImage(
                        piece == null ? null : imageProvider.getImage(piece.piece(), piece.color()));
            }
        }
    }

    /**
     * Sets all cells into their idle state, but shows the active check
     */
    public void repaintBackground() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                this.chessboard[i][j].refreshBackground();
            }
        }

        if (checkedKing != null) {
            markCheckedKingsField();
        }
    }

    /**
     * Refreshes the board coloring, then marks chosen piece and possible destinations
     */
    public void choosePiece(Position picked, Collection<Position> legalTargets) {
        repaintBackground();
        chessboard[picked.x()][picked.y()].setChosenBackground();
        for (Position target : legalTargets) {
            chessboard[target.x()][target.y()].setClickableBackground();
        }
    }

    /**
     * Prints last move into text area
     */
    private void appendMoveToLedger(Color player, String notation) {
        this.movesTextArea.setEditable(true);
        if (player == Color.WHITE) {
            this.movesTextArea.appendText(moveCounter + ". " + notation);
        } else {
            this.movesTextArea.appendText(" " + notation + "\n");
            moveCounter++;
        }
        this.movesTextArea.setEditable(false);
    }

    private Position findCheckedKing(Color color, Piece[][] board) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece piece = board[i][j];
                if (piece != null && piece.piece() == PieceType.KING && piece.color() == color) {
                    return new Position(i, j);
                }
            }
        }
        throw new IllegalStateException("King must be on the board");
    }

    private void markCheckedKingsField() {
        this.chessboard[checkedKing.x()][checkedKing.y()].setCheckedBackground();
    }

    // Setters

    /**
     * Provides reference to controller and initializes cells of the board
     */
    public void setController(GameController gameController) {
        this.gameController = gameController;

        this.chessboard = new ChessboardCell[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                chessboard[i][j] = new ChessboardCell(i, j, gameController);
                chessBoardGrid.add(chessboard[i][j].getPane(), i, j);
            }
        }
    }

    @FXML
    public void handleSurrenderAction(ActionEvent ignored) {
        gameController.surrenderButtonOnClick();
    }

    @FXML
    public void handleDrawAction(ActionEvent ignored) {
        gameController.drawButtonOnClick();
    }

}
