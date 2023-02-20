package com.pmarshall.chessgame.client.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class GameEndedController {

    private Stage primaryStage;
    private Stage stage;

    @FXML
    private Label gameResultLabel;

    public static void initRootLayout(Stage primaryStage, String message) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(GameEndedController.class.getResource("/view/game_ended_screen.fxml"));
        Parent rootLayout = loader.load();
        GameEndedController controller = loader.getController();

        Scene scene = new Scene(rootLayout);
        Stage stage = new Stage();

        controller.injectDependencies(primaryStage, stage);
        controller.gameResultLabel.setText(message);

        stage.setTitle("End game dialog");
        stage.setScene(scene);
        stage.showAndWait();
    }

    private void injectDependencies(Stage primaryStage, Stage stage) {
        this.primaryStage = primaryStage;
        this.stage = stage;
    }

    @FXML
    public void buttonClickedHandler(MouseEvent event) {
        stage.close();
        MenuController.initRootLayout(primaryStage);
    }
}
