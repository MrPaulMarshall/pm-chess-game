package com.pmarshall.chessgame.controller;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class DrawRequestController {

    private final GameController gameController;

    public DrawRequestController(GameController gameController) {
        this.gameController = gameController;
    }

    /**
     * Creates dialog window and doesn't wait for player's decision
     */
    public void displayWindow() {
        // create new window
        Stage stage = new Stage();
        stage.setTitle("Draw requested dialog");
        stage.setResizable(false);

        // horizontal box with text (at the bottom of the window)
        HBox labelHBox = new HBox();
        Label label = new Label("The opponent has proposed a draw");
        label.setFont(Font.font(16));
        labelHBox.getChildren().add(label);
        labelHBox.setAlignment(Pos.CENTER);

        // horizontal box with possible pieces (clickable)
        HBox buttonsHBox = new HBox();
        buttonsHBox.setMaxHeight(70);
        buttonsHBox.spacingProperty().setValue(10);

        {
            Button button = new Button("Accept");
            button.setOnMouseClicked(e -> {
                gameController.acceptDraw();
                stage.close();
            });
            button.setPrefWidth(70);
            button.setPrefHeight(70);

            buttonsHBox.getChildren().add(button);
        }

        {
            Button button = new Button("Reject");
            button.setOnMouseClicked(e -> {
                gameController.rejectDraw();
                stage.close();
            });
            button.setPrefWidth(70);
            button.setPrefHeight(70);

            buttonsHBox.getChildren().add(button);
        }

        // window-sized container
        BorderPane root = new BorderPane();
        root.setCenter(buttonsHBox);
        root.setBottom(labelHBox);

        // creates scene
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setWidth(325);
        stage.setHeight(180);

        // display and wait for player's decision
        stage.show();
    }

}
