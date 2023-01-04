package com.pmarshall.chessgame.presenter;

import com.pmarshall.chessgame.controller.GameController;
import javafx.geometry.Insets;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

/**
 * @author Paweł Marszał
 *
 * Represents single cell on the board from the GUI's perspective
 */
public class ChessboardCell {

    /**
     * Background with different colors, to inform user of current state of the game
     */
    private static final Background WHITE_BACKGROUND = new Background(
            new BackgroundFill(Color.valueOf("#EEEED5"), CornerRadii.EMPTY, Insets.EMPTY));
    private static final Background BLACK_BACKGROUND = new Background(
            new BackgroundFill(Color.valueOf("#7D945D"), CornerRadii.EMPTY, Insets.EMPTY));

    private static final Background WHITE_CLICKABLE_BACKGROUND = new Background(
            new BackgroundFill(Color.valueOf("#abab9a"), CornerRadii.EMPTY, Insets.EMPTY));
    private static final Background BLACK_CLICKABLE_BACKGROUND = new Background(
            new BackgroundFill(Color.valueOf("#64764a"), CornerRadii.EMPTY, Insets.EMPTY));

    private static final Background MARKED_BACKGROUND = new Background(
            new BackgroundFill(Color.valueOf("#5BBDD6"), CornerRadii.EMPTY, Insets.EMPTY));
    private static final Background CHECKED_BACKGROUND = new Background(
            new BackgroundFill(Color.valueOf("#E2514C"), CornerRadii.EMPTY, Insets.EMPTY));

    /**
     * Reference to controller
     */
    private final GameController gameController;

    /**
     * Pane that represents the cell, it contains background and potentially piece's image
     */
    private final Pane pane;

    /**
     * Object that displays piece's image provided by the controller
     */
    private final ImageView imageView;

    /**
     * Two backgrounds: one in idle state, and one that tells that piece can move to this cell
     */
    private final Background normalBackground;
    private final Background clickableBackground;

    /**
     * Create ChessboardCell object
     * @param i column coordinate
     * @param j row coordinate
     * @param gameController reference to controller
     */
    public ChessboardCell(int i, int j, GameController gameController) {
        this.pane = new StackPane();
        this.gameController = gameController;
        this.imageView = new ImageView();

        this.pane.setMaxWidth(50);
        this.pane.setMinWidth(50);
        this.pane.setMaxHeight(50);
        this.pane.setMinHeight(50);
        this.pane.getChildren().add(this.imageView);

        // determine if this cell should be light or dark
        if ((i + j) % 2 == 0) {
            this.normalBackground = WHITE_BACKGROUND;
            this.clickableBackground = WHITE_CLICKABLE_BACKGROUND;
        } else {
            this.normalBackground = BLACK_BACKGROUND;
            this.clickableBackground = BLACK_CLICKABLE_BACKGROUND;
        }

        this.pane.setBackground(this.normalBackground);

        this.pane.setOnMouseClicked(e -> this.gameController.boardCellOnClick(i, j));
    }

    public Pane getPane() {
        return this.pane;
    }

    /**
     * @param image image to display
     */
    public void setImage(Image image) {
        this.imageView.setImage(image);
    }

    /**
     * Sets background to idle state
     */
    public void refreshBackground() {
        this.pane.setBackground(this.normalBackground);
    }

    /**
     * Marks that this cell is currently chosen
     */
    public void setChosenBackground() {
        this.pane.setBackground(MARKED_BACKGROUND);
    }

    /**
     * Marks that king on this cell is currently in check
     */
    public void setCheckedBackground() {
        this.pane.setBackground(CHECKED_BACKGROUND);
    }

    /**
     * Marks that currently chosen piece can move to this cell
     */
    public void setClickableBackground() {
        this.pane.setBackground(this.clickableBackground);
    }
}
