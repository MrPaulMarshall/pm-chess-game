package com.pmarshall.chessgame.client;

import com.pmarshall.chessgame.client.controller.MenuController;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Main class that starts lifecycle of the game
 */
public class App extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Chess game by Paweł Marszał");
        MenuController.initRootLayout(primaryStage);
    }

}
