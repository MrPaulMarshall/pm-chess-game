package com.pmarshall.chessgame.client.controller;

import com.pmarshall.chessgame.client.App;
import com.pmarshall.chessgame.client.FXMLUtils;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class DrawRequestController {

    private final Stage stage;

    private final RemoteGameController gameController;

    private DrawRequestController(Stage stage, RemoteGameController gameController) {
        this.stage = stage;
        this.gameController = gameController;
    }

    /**
     * Creates dialog window and doesn't wait for player's decision
     */
    public static void initRootLayout(RemoteGameController gameController) {
        Stage stage = new Stage();
        DrawRequestController controller = new DrawRequestController(stage, gameController);
        Parent root = FXMLUtils.load(controller, "/view/draw_dialog_screen.fxml");

        // creates scene
        stage.setTitle("Draw requested dialog");
        stage.setScene(new Scene(root));
        stage.setResizable(false);
        stage.initOwner(App.primaryStage());
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
