package com.pmarshall.chessgame.controller;

import com.pmarshall.chessgame.model.properties.Color;
import com.pmarshall.chessgame.remote.RemoteGameProxy;
import com.pmarshall.chessgame.remote.ServerProxy;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * @author Paweł Marszał
 *
 * Controller of game view in remote mode
 */
public class RemoteGameController extends GameControllerBase {

    // TODO: add Chat + input textfield

    private ServerProxy serverProxy;

    /**
     * Initializes game and displays view
     * @throws IOException if the view cannot be loaded
     */
    public static void initRootLayout(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(RemoteGameController.class.getResource("/view/remote_game_screen.fxml"));

        BorderPane rootLayout = loader.load();
        Scene scene = new Scene(rootLayout);

        RemoteGameController controller = loader.getController();
        RemoteGameProxy game = RemoteGameProxy.connectToServer(controller);
        controller.injectDependencies(primaryStage, game);
        controller.createBoardGrid(game.localPlayer() == Color.WHITE);
        controller.refreshAfterMove(game.currentPlayer(), game.getBoardWithPieces());

        primaryStage.setTitle("Chess board");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void injectDependencies(Stage primaryStage, RemoteGameProxy game) {
        this.primaryStage = primaryStage;
        this.game = game;
        this.serverProxy = game;
    }

    @FXML
    public void handleSurrenderAction(ActionEvent ignored) throws InterruptedException {
        serverProxy.surrender();
    }

    @FXML
    public void handleDrawAction(ActionEvent ignored) throws InterruptedException {
        serverProxy.proposeDraw();
    }

    public void showDrawRequestedWindow() {
        // TODO: disable interacting with the board
        DrawRequestController controller = new DrawRequestController(this);
        controller.displayWindow();
    }

    public void acceptDraw() {
        // TODO: serverProxy.acceptDraw()
    }

    public void rejectDraw() {
        // TODO: serverProxy.rejectDraw()
    }

}
