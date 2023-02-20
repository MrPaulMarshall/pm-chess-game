package com.pmarshall.chessgame.client.controller;

import com.pmarshall.chessgame.client.FXMLUtils;
import com.pmarshall.chessgame.model.properties.Color;
import com.pmarshall.chessgame.model.service.Game;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.ServiceLoader;

/**
 * @author Paweł Marszał
 *
 * Represents controller of main view of the application
 */
public class LocalGameController extends GameControllerBase {

    private static final Logger log = LoggerFactory.getLogger(LocalGameController.class);

    private LocalGameController(Stage primaryStage, Game game) {
        this.primaryStage = primaryStage;
        this.game = game;
    }

    /**
     * Initializes game and displays view
     */
    public static void initRootLayout(Stage primaryStage) {
        Optional<Game> loadedGameService = ServiceLoader.load(Game.class).findFirst();
        if (loadedGameService.isEmpty()) {
            log.error("Game service was not provided, cannot use local mode");
            return;
        }
        Game game = loadedGameService.get();

        LocalGameController controller = new LocalGameController(primaryStage, game);
        Parent root = FXMLUtils.load(controller, "/view/local_game_screen.fxml");

        Scene scene = new Scene(root);
        primaryStage.setTitle("Chess board");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @FXML
    private void initialize() {
        createBoardGrid(true);
        refreshBoard(game.currentPlayer(), game.getBoardWithPieces());
    }

    @FXML
    private void handleSurrenderAction(ActionEvent ignored) {
        endGame(game.currentPlayer().next());
    }

    @FXML
    private void handleDrawAction(ActionEvent ignored) {
        endGame(null);
    }

    @Override
    protected boolean arePlayersPiecesDisabled(Color player) {
        return false;
    }
}
