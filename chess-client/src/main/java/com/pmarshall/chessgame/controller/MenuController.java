package com.pmarshall.chessgame.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import com.pmarshall.chessgame.presenter.MenuView;

/**
 * @author Paweł Marszał
 *
 * Controller that initializes and displays 'welcome' screen
 */
public class MenuController {

    private final Stage primaryStage;

    public MenuController(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    /**
     * Loads, initializes and displays 'welcome' screen
     * @throws Exception if anything goes wrong
     */
    public void initRootLayout() throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(MenuController.class
                .getResource("/view/menu_screen.fxml"));
        BorderPane page = loader.load();

        Stage dialogStage = new Stage();
        dialogStage.setTitle("Chess game - welcome screen");
        dialogStage.setResizable(false);
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(primaryStage);
        Scene scene = new Scene(page);
        dialogStage.setScene(scene);

        MenuView menuView = loader.getController();
        menuView.setDialogStage(dialogStage);
        menuView.setPrimaryStage(primaryStage);

        dialogStage.showAndWait();
    }

}
