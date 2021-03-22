package chessgame;

import chessgame.controller.AppController;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Chess game by Paweł Marszał");
        AppController appController = new AppController(primaryStage);
        appController.initRootLayout();
    }

}
