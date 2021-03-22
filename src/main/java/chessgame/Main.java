package chessgame;

import chessgame.controller.WelcomeScreenController;
import javafx.application.Application;
import javafx.stage.Stage;

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
