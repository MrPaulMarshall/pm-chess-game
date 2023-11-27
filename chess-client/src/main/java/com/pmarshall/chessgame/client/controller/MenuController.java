package com.pmarshall.chessgame.client.controller;

import com.pmarshall.chessgame.client.App;
import com.pmarshall.chessgame.client.FXMLUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

/**
 * @author Paweł Marszał
 *
 * Controller that initializes and displays 'welcome' screen
 */
public class MenuController {

    @FXML
    private StackPane imagePane;

    /**
     * Loads, initializes and displays 'welcome' screen
     */
    public static void initRootLayout() {
        MenuController controller = new MenuController();
        Parent root = FXMLUtils.load(controller, "/view/menu_screen.fxml");
        App.primaryStage().setScene(new Scene(root));
    }

    @FXML
    private void initialize() {
        ImageView imageView = new ImageView();
        imageView.setImage(
                new Image("icons/green-knight-icon.png", 240, 240, false, true, false)
        );
        imagePane.getChildren().add(imageView);
    }

    @FXML
    private void handleLocalAction(ActionEvent ignored) {
        LocalGameController.initRootLayout();
    }

    @FXML
    private void handleRemoteAction(ActionEvent ignored) {
        MatchQueueController.initRootLayout();
    }

    @FXML
    private void handleCancelAction(ActionEvent ignored) {
        App.primaryStage().close();
    }
}
