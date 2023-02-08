package com.pmarshall.chessgame.client.controller;

import com.pmarshall.chessgame.engine.game.InMemoryChessGame;
import com.pmarshall.chessgame.model.properties.Color;
import com.pmarshall.chessgame.model.service.Game;
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
 * Represents controller of main view of the application
 */
public class LocalGameController extends GameControllerBase {

    /**
     * Initializes game and displays view
     * @throws IOException if anything goes wrong
     */
    public static void initRootLayout(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(LocalGameController.class.getResource("/view/local_game_screen.fxml"));

        BorderPane rootLayout = loader.load();
        Scene scene = new Scene(rootLayout);

        LocalGameController controller = loader.getController();
        Game game = new InMemoryChessGame();
        controller.injectDependencies(primaryStage, game);
        controller.createBoardGrid(true);
        controller.refreshBoard(game.currentPlayer(), game.getBoardWithPieces());

        primaryStage.setTitle("Chess board");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void injectDependencies(Stage primaryStage, Game game) {
        this.primaryStage = primaryStage;
        this.game = game;
    }

    @FXML
    public void handleSurrenderAction(ActionEvent ignored) {
        endGame(game.currentPlayer().next());
    }

    @FXML
    public void handleDrawAction(ActionEvent ignored) {
        endGame(null);
    }

    @Override
    protected boolean arePlayersPiecesDisabled(Color player) {
        return false;
    }
}
