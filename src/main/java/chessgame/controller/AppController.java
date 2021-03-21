package chessgame.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import chessgame.presenter.WelcomeScreenPresenter;

public class AppController {

    private Stage primaryStage;

    public AppController(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void initRootLayout() throws Exception {
        showWelcomeScreen();
    }

    private void showWelcomeScreen() throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(AppController.class
                .getResource("/view/welcome_screen.fxml"));
        BorderPane page = loader.load();

        Stage dialogStage = new Stage();
        dialogStage.setTitle("Chess game - welcome screen");
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(primaryStage);
        Scene scene = new Scene(page);
        dialogStage.setScene(scene);

        WelcomeScreenPresenter presenter = loader.getController();
        presenter.setDialogStage(dialogStage);
        presenter.setPrimaryStage(primaryStage);

        dialogStage.showAndWait();
    }

    private void showBoardScreen() throws Exception {
        BoardScreenController boardScreenController = new BoardScreenController(this, primaryStage);
        boardScreenController.initRootLayout();
    }
}
