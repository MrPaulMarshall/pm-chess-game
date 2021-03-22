package chessgame.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import chessgame.presenter.WelcomeScreenView;

public class WelcomeScreenController {

    private final Stage primaryStage;

    public WelcomeScreenController(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void initRootLayout() throws Exception {
        showWelcomeScreen();
    }

    private void showWelcomeScreen() throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(WelcomeScreenController.class
                .getResource("/view/welcome_screen.fxml"));
        BorderPane page = loader.load();

        Stage dialogStage = new Stage();
        dialogStage.setTitle("Chess game - welcome screen");
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
