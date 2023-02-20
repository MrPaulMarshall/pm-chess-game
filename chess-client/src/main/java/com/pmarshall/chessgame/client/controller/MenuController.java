package com.pmarshall.chessgame.client.controller;

import com.pmarshall.chessgame.client.FXMLUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * @author Paweł Marszał
 *
 * Controller that initializes and displays 'welcome' screen
 */
public class MenuController {

    private final Stage primaryStage;

    @FXML
    private StackPane imagePane;

    private MenuController(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    /**
     * Loads, initializes and displays 'welcome' screen
     */
    public static void initRootLayout(Stage primaryStage) {
        MenuController controller = new MenuController(primaryStage);
        Parent root = FXMLUtils.load(controller, "/view/menu_screen.fxml");

        Scene scene = new Scene(root);
        primaryStage.setTitle("Chess game - welcome screen");
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @FXML
    private void initialize() {
        ImageView imageView = new ImageView();
        imageView.setImage(
                new Image("images/black-queen.png", 240, 240, false, true, false)
        );
        imagePane.getChildren().add(imageView);
    }

    @FXML
    private void handleLocalAction(ActionEvent ignored) {
        LocalGameController.initRootLayout(primaryStage);
    }

    @FXML
    private void handleRemoteAction(ActionEvent ignored) {
        MatchQueueController.initRootLayout(primaryStage);
    }

    @FXML
    private void handleCancelAction(ActionEvent ignored) {
        primaryStage.close();
    }
}
