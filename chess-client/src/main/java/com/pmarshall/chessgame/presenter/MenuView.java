package com.pmarshall.chessgame.presenter;

import com.pmarshall.chessgame.controller.GameController;
import com.pmarshall.chessgame.model.game.InMemoryChessGame;
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
public class MenuView {

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
    public void handleLocalAction(ActionEvent ignored) throws Exception {
        dialogStage.close();
        GameController gameController = new GameController(primaryStage, new InMemoryChessGame());
        gameController.initRootLayout();
    }

    @FXML
    public void handleRemoteAction(ActionEvent ignored) throws Exception {
        dialogStage.close();
        // TODO: GameController should receive arguments differentiating remote connection from local play
        GameController gameController = new GameController(primaryStage, new InMemoryChessGame());
        gameController.initRootLayout();
    }

    @FXML
    public void handleCancelAction(ActionEvent ignored) {
        dialogStage.close();
        primaryStage.close();
    }

}
