package com.pmarshall.chessgame.presenter;

import javafx.geometry.Insets;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.util.function.Consumer;

/**
 * @author Paweł Marszał
 *
 * Represents single cell on the board from the GUI's perspective
 */
public class ChessboardCell {

    /**
     * Background with different colors, to inform user of current state of the game
     */
    private static final Background LIGHT_BACKGROUND = new Background(
            new BackgroundFill(Color.valueOf("#EEEED5"), CornerRadii.EMPTY, Insets.EMPTY));
    private static final Background DARK_BACKGROUND = new Background(
            new BackgroundFill(Color.valueOf("#7D945D"), CornerRadii.EMPTY, Insets.EMPTY));

    private static final Background LIGHT_CLICKABLE_BACKGROUND = new Background(
            new BackgroundFill(Color.valueOf("#abab9a"), CornerRadii.EMPTY, Insets.EMPTY));
    private static final Background DARK_CLICKABLE_BACKGROUND = new Background(
            new BackgroundFill(Color.valueOf("#64764a"), CornerRadii.EMPTY, Insets.EMPTY));

    private static final Background MARKED_BACKGROUND = new Background(
            new BackgroundFill(Color.valueOf("#5BBDD6"), CornerRadii.EMPTY, Insets.EMPTY));
    private static final Background CHECKED_BACKGROUND = new Background(
            new BackgroundFill(Color.valueOf("#E2514C"), CornerRadii.EMPTY, Insets.EMPTY));

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
     */
    public ChessboardCell(boolean lightSquare, Consumer<MouseEvent> clickEventHandler) {
        this.pane = new StackPane();
        this.imageView = new ImageView();

        this.pane.setMaxWidth(50);
        this.pane.setMinWidth(50);
        this.pane.setMaxHeight(50);
        this.pane.setMinHeight(50);
        this.pane.getChildren().add(this.imageView);

        // determine if this cell should be light or dark
        if (lightSquare) {
            this.normalBackground = LIGHT_BACKGROUND;
            this.clickableBackground = LIGHT_CLICKABLE_BACKGROUND;
        } else {
            this.normalBackground = DARK_BACKGROUND;
            this.clickableBackground = DARK_CLICKABLE_BACKGROUND;
        }

        this.pane.setBackground(this.normalBackground);

        this.pane.setOnMouseClicked(clickEventHandler::accept);
    }

    public Pane getPane() {
        return this.pane;
    }

    /**
     * @param image image to display
     */
    public void setImage(Image image) {
        imageView.setImage(image);
    }

    /**
     * Sets background to idle state
     */
    public void refreshBackground() {
        pane.setBackground(normalBackground);
    }

    /**
     * Marks that this cell is currently chosen
     */
    public void setChosenBackground() {
        pane.setBackground(MARKED_BACKGROUND);
    }

    /**
     * Marks that king on this cell is currently in check
     */
    public void setCheckedBackground() {
        pane.setBackground(CHECKED_BACKGROUND);
    }

    /**
     * Marks that currently chosen piece can move to this cell
     */
    public void setClickableBackground() {
        pane.setBackground(clickableBackground);
    }
}
