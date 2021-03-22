package chessgame.controller;

import chessgame.model.properties.PlayerColor;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class ChoosePromotionPieceController {

    private static final Background NORMAL_BACKGROUND = new Background(
            new BackgroundFill(Color.GRAY, CornerRadii.EMPTY, Insets.EMPTY));

    private int chosenPieceIndex;

    private final String[] possiblePieces = {"queen", "knight", "rook", "bishop"};

    public String askForPromotionPiece(PlayerColor color) {
        this.chosenPieceIndex = -1;

        Stage stage = new Stage();
        stage.setTitle("Promoting piece dialog");

        HBox labelHBox = new HBox();
        Label label = new Label("To choose piece double-click on it");
        label.setFont(Font.font(16));
        labelHBox.getChildren().add(label);
        labelHBox.setAlignment(Pos.CENTER);

        HBox piecesHBox = new HBox();
        piecesHBox.setMaxHeight(70);
        piecesHBox.spacingProperty().setValue(10);

        for (int i = 0; i < possiblePieces.length; i++) {
            String path = "images/" + (color == PlayerColor.WHITE ? "white" : "black") + "-" + possiblePieces[i] + ".png";
            Image image = new Image(path, 70, 70, false,true, false);

            StackPane pane = new StackPane();
            pane.setBackground(NORMAL_BACKGROUND);
            pane.getChildren().add(new ImageView(image));

            final int index = i;
            pane.setOnMouseClicked(e -> {
                if (index == this.chosenPieceIndex) {
                    stage.close();
                } else {
                    this.chosenPieceIndex = index;
                }
            });
            pane.setPrefWidth(70);
            pane.setPrefHeight(70);

            piecesHBox.getChildren().add(pane);
        }

        BorderPane root = new BorderPane();
        root.setCenter(piecesHBox);
        root.setBottom(labelHBox);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setWidth(325);
        stage.setHeight(180);
        stage.showAndWait();

        return this.possiblePieces[this.chosenPieceIndex];
    }

}
