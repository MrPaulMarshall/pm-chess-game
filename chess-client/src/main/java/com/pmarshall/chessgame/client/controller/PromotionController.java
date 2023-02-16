package com.pmarshall.chessgame.client.controller;

import com.pmarshall.chessgame.model.properties.Color;
import com.pmarshall.chessgame.model.properties.PieceType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * @author Paweł Marszał
 * <p>
 * Class that creates and displays dialog when player chooses piece during promotion
 * It combines role of controller and the view (because it is small)
 */
public class PromotionController {

    /**
     * Stores information about player's decision
     */
    private int chosenPieceIndex = -1;

    /**
     * Pieces that player can choose
     */
    private static final PieceType[] possiblePieces = {
            PieceType.QUEEN, PieceType.ROOK, PieceType.BISHOP, PieceType.KNIGHT};

    @FXML
    private GridPane imagesGrid;

    public static PieceType askForPromotionPiece(Stage primaryStage, Color color) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(PromotionController.class.getResource("/view/promotion_dialog_screen.fxml"));
        Parent rootLayout = loader.load();
        PromotionController controller = loader.getController();

        Scene scene = new Scene(rootLayout);
        Stage stage = new Stage();

        for (int i = 0; i < possiblePieces.length; i++) {
            String path = "images/"
                    + color.name().toLowerCase() + "-" + possiblePieces[i].name().toLowerCase() + ".png";

            StackPane pane = (StackPane) controller.imagesGrid.getChildren().get(i);
            ImageView view = (ImageView) pane.getChildren().get(0);
            view.setImage(new Image(path));

            final int index = i;
            pane.setOnMouseClicked(e -> {
                if (index == controller.chosenPieceIndex) {
                    stage.close();
                } else {
                    controller.chosenPieceIndex = index;
                }
            });
        }

        stage.setTitle("Promoting piece dialog");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.initOwner(primaryStage);
        stage.initModality(Modality.WINDOW_MODAL);

        stage.showAndWait();

        return possiblePieces[controller.chosenPieceIndex];
    }

    @FXML
    public void initialize() {
    }
}
