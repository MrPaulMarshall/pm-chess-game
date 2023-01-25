package com.pmarshall.chessgame.client.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * @author Paweł Marszał
 *
 * Controller that initializes and displays 'welcome' screen
 */
public class MenuController {

    private Stage primaryStage;

    @FXML
    private StackPane imagePane;

    @FXML
    public void initialize() {
        ImageView imageView = new ImageView();
        imageView.setImage(
                new Image("images/black-queen.png", 240, 240, false, true, false)
        );
        imagePane.getChildren().add(imageView);
    }

    @FXML
    public void handleLocalAction(ActionEvent ignored) throws Exception {
        LocalGameController.initRootLayout(primaryStage);
    }

    @FXML
    public void handleRemoteAction(ActionEvent ignored) throws IOException {
        RemoteGameController.initRootLayout(primaryStage);
    }

    @FXML
    public void handleCancelAction(ActionEvent ignored) {
        primaryStage.close();
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    /**
     * Loads, initializes and displays 'welcome' screen
     * @throws IOException if anything goes wrong
     */
    public static void initRootLayout(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(MenuController.class.getResource("/view/menu_screen.fxml"));
        BorderPane page = loader.load();
        Scene scene = new Scene(page);

        MenuController controller = loader.getController();
        controller.setPrimaryStage(primaryStage);

        primaryStage.setTitle("Chess game - welcome screen");
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

}
