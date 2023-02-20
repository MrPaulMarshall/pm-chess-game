package com.pmarshall.chessgame.client.controller;

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
class ChessboardCell {

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
    ChessboardCell(boolean lightSquare, Consumer<MouseEvent> clickEventHandler) {
        this.pane = new StackPane();
        this.imageView = new ImageView();

        pane.prefHeightProperty().bindBidirectional(pane.prefWidthProperty());

        pane.maxHeightProperty().bind(pane.prefHeightProperty());
        pane.minHeightProperty().bind(pane.prefHeightProperty());
        pane.maxWidthProperty().bind(pane.prefWidthProperty());
        pane.minWidthProperty().bind(pane.prefWidthProperty());

        pane.getChildren().add(imageView);

        imageView.fitHeightProperty().bind(pane.prefHeightProperty());
        imageView.fitWidthProperty().bind(pane.prefWidthProperty());

        // determine if this cell should be light or dark
        if (lightSquare) {
            this.normalBackground = LIGHT_BACKGROUND;
            this.clickableBackground = LIGHT_CLICKABLE_BACKGROUND;
        } else {
            this.normalBackground = DARK_BACKGROUND;
            this.clickableBackground = DARK_CLICKABLE_BACKGROUND;
        }

        pane.setBackground(normalBackground);

        pane.setOnMouseClicked(clickEventHandler::accept);
    }

    Pane getPane() {
        return pane;
    }

    /**
     * @param image image to display
     */
    void setImage(Image image) {
        imageView.setImage(image);
    }

    /**
     * Sets background to idle state
     */
    void refreshBackground() {
        pane.setBackground(normalBackground);
    }

    /**
     * Marks that this cell is currently chosen
     */
    void setChosenBackground() {
        pane.setBackground(MARKED_BACKGROUND);
    }

    /**
     * Marks that king on this cell is currently in check
     */
    void setCheckedBackground() {
        pane.setBackground(CHECKED_BACKGROUND);
    }

    /**
     * Marks that currently chosen piece can move to this cell
     */
    void setClickableBackground() {
        pane.setBackground(clickableBackground);
    }
}
