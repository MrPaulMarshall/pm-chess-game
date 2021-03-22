package chessgame.presenter;

import chessgame.controller.WelcomeScreenController;
import chessgame.controller.BoardScreenController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class WelcomeScreenView {

    private Stage dialogStage;
    private Stage primaryStage;
    private WelcomeScreenController welcomeScreenController;

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
        BoardScreenController boardScreenController = new BoardScreenController(primaryStage);
        boardScreenController.initRootLayout();
    }

    @FXML
    public void handleCancelAction(ActionEvent e) {
        dialogStage.close();
        primaryStage.close();
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void setAppController(WelcomeScreenController welcomeScreenController) {
        this.welcomeScreenController = welcomeScreenController;
    }
}
