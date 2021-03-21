package chessgame.presenter;

import chessgame.controller.AppController;
import chessgame.controller.BoardScreenController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class WelcomeScreenPresenter {

    private Stage dialogStage;
    private Stage primaryStage;
    private AppController appController;

    @FXML
    private StackPane imagePane;

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }


    @FXML
    public void initialize() {
        ImageView imageView = new ImageView();
        imageView.setImage(
                new Image("images/black-queen.png", 240, 240, false, true, false)
        );
        this.imagePane.getChildren().add(imageView);
    }


    @FXML
    public void handleOkAction(ActionEvent e) throws Exception {
        dialogStage.close();
        BoardScreenController boardScreenController = new BoardScreenController(appController, primaryStage);
        boardScreenController.initRootLayout();
    }

    @FXML
    public void handleCancelAction(ActionEvent e) throws Exception {
        dialogStage.close();
        System.exit(0);
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void setAppController(AppController appController) {
        this.appController = appController;
    }
}
