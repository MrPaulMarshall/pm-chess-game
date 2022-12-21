package com.pmarshall.chessgame;

import com.pmarshall.chessgame.controller.WelcomeScreenController;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * @author Paweł Marszał https://github.com/MrPaulMarshall
 *
 * Main class that starts lifecycle of the game
 */
public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Chess game by Paweł Marszał");
        WelcomeScreenController welcomeScreenController = new WelcomeScreenController(primaryStage);
        welcomeScreenController.initRootLayout();
    }

}
