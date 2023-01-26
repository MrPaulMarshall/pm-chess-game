package com.pmarshall.chessgame.client.controller;

import com.pmarshall.chessgame.model.properties.Color;
import com.pmarshall.chessgame.model.properties.PieceType;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * @author Paweł Marszał
 * <p>
 * Class that creates and displays dialog when player chooses piece during promotion
 * It combines role of controller and the view (because it is small)
 */
public class PromotionController {

    /**
     * Background on which pieces to choose are displayed
     */
    private static final Background NORMAL_BACKGROUND = new Background(
            new BackgroundFill(javafx.scene.paint.Color.GRAY, CornerRadii.EMPTY, Insets.EMPTY));

    private final Stage primaryStage;

    /**
     * Stores information about player's decision
     */
    private int chosenPieceIndex;

    /**
     * Pieces that player can choose
     */
    private final PieceType[] possiblePieces = {PieceType.QUEEN, PieceType.ROOK, PieceType.BISHOP, PieceType.KNIGHT};

    public PromotionController(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    /**
     * Creates dialog window and waits for player's decision
     * @param color determines which player needs a piece
     * @return piece that player has chosen
     */
    public PieceType askForPromotionPiece(Color color) {
        chosenPieceIndex = -1;

        // create new window
        Stage stage = new Stage();
        stage.setTitle("Promoting piece dialog");
        stage.setResizable(false);

        // horizontal box with text (at the bottom of the window)
        HBox labelHBox = new HBox();
        Label label = new Label("To choose piece double-click on it");
        label.setFont(Font.font(16));
        labelHBox.getChildren().add(label);
        labelHBox.setAlignment(Pos.CENTER);

        // horizontal box with possible pieces (clickable)
        HBox piecesHBox = new HBox();
        piecesHBox.setMaxHeight(70);
        piecesHBox.spacingProperty().setValue(10);

        for (int i = 0; i < possiblePieces.length; i++) {
            String path = "images/"
                    + color.name().toLowerCase() + "-" + possiblePieces[i].name().toLowerCase() + ".png";
            Image image = new Image(path, 70, 70, false,true, false);

            StackPane pane = new StackPane();
            pane.setBackground(NORMAL_BACKGROUND);
            pane.getChildren().add(new ImageView(image));

            final int index = i;
            pane.setOnMouseClicked(e -> {
                if (index == chosenPieceIndex) {
                    stage.close();
                } else {
                    chosenPieceIndex = index;
                }
            });
            pane.setPrefWidth(70);
            pane.setPrefHeight(70);

            piecesHBox.getChildren().add(pane);
        }

        // window-sized container
        BorderPane root = new BorderPane();
        root.setCenter(piecesHBox);
        root.setBottom(labelHBox);

        // creates scene
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setWidth(325);
        stage.setHeight(180);

        stage.initOwner(primaryStage);
        stage.initModality(Modality.WINDOW_MODAL);

        // display and wait for player's decision
        stage.showAndWait();

        // return string indicator of player's decision
        return possiblePieces[chosenPieceIndex];
    }

}
