package com.pmarshall.chessgame.client.controller;

import com.pmarshall.chessgame.client.FXMLUtils;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class GameEndedController {

    private final Stage stage;

    @FXML
    private Label gameResultLabel;

    private GameEndedController(Stage stage) {
        this.stage = stage;
    }

    public static void initRootLayout(String message) {
        Stage stage = new Stage();
        GameEndedController controller = new GameEndedController(stage);
        Parent root = FXMLUtils.load(controller, "/view/game_ended_screen.fxml");

        controller.gameResultLabel.setText(message);

        stage.setTitle("End game dialog");
        stage.setScene(new Scene(root));
        stage.showAndWait();
    }

    @FXML
    private void buttonClickedHandler() {
        stage.close();
        MenuController.initRootLayout();
    }
}
