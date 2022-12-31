package com.pmarshall.chessgame.presenter;

import com.pmarshall.chessgame.controller.BoardScreenController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * @author Paweł Marszał
 *
 * Class that represents screen that welcomes players before game starts
 */
public class WelcomeScreenView {

    private Stage dialogStage;
    private Stage primaryStage;

    @FXML
    private StackPane imagePane;

    // Setters

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
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
    public void handleLocalAction(ActionEvent e) throws Exception {
        dialogStage.close();
        BoardScreenController boardScreenController = new BoardScreenController(primaryStage);
        boardScreenController.initRootLayout();
    }

    @FXML
    public void handleRemoteAction(ActionEvent e) throws Exception {
        dialogStage.close();
        // TODO: BoardScreenController should receive arguments differentiating remote connection from local play
        BoardScreenController boardScreenController = new BoardScreenController(primaryStage);
        boardScreenController.initRootLayout();
    }

    @FXML
    public void handleCancelAction(ActionEvent e) {
        dialogStage.close();
        primaryStage.close();
    }

}
