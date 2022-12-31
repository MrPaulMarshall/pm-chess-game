package com.pmarshall.chessgame.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import com.pmarshall.chessgame.presenter.WelcomeScreenView;

/**
 * @author Paweł Marszał
 *
 * Controller that initializes and displays 'welcome' screen
 */
public class WelcomeScreenController {

    private final Stage primaryStage;

    public WelcomeScreenController(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    /**
     * Loads, initializes and displays 'welcome' screen
     * @throws Exception if anything goes wrong
     */
    public void initRootLayout() throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(WelcomeScreenController.class
                .getResource("/view/welcome_screen.fxml"));
        BorderPane page = loader.load();

        Stage dialogStage = new Stage();
        dialogStage.setTitle("Chess game - welcome screen");
        dialogStage.setResizable(false);
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(primaryStage);
        Scene scene = new Scene(page);
        dialogStage.setScene(scene);

        WelcomeScreenView welcomeScreenView = loader.getController();
        welcomeScreenView.setDialogStage(dialogStage);
        welcomeScreenView.setPrimaryStage(primaryStage);

        dialogStage.showAndWait();
    }

}
