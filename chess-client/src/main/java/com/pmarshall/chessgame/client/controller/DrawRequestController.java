package com.pmarshall.chessgame.client.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class DrawRequestController {

    private Stage stage;
    private RemoteGameController gameController;

    private void injectDependencies(Stage stage, RemoteGameController gameController) {
        this.stage = stage;
        this.gameController = gameController;
    }

    /**
     * Creates dialog window and doesn't wait for player's decision
     */
    public static void initRootLayout(Stage primaryStage, RemoteGameController gameController) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(DrawRequestController.class.getResource("/view/draw_dialog_screen.fxml"));
        Parent rootLayout = loader.load();
        DrawRequestController controller = loader.getController();

        Stage stage = new Stage();
        controller.injectDependencies(stage, gameController);

        // creates scene
        Scene scene = new Scene(rootLayout);
        stage.setTitle("Draw requested dialog");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.initOwner(primaryStage);
        stage.initModality(Modality.WINDOW_MODAL);

        // display and wait for player's decision
        stage.showAndWait();
    }

    @FXML
    private void acceptDraw() {
        gameController.acceptDraw();
        stage.close();
    }

    @FXML
    private void rejectDraw() {
        gameController.rejectDraw();
        stage.close();
    }
}
