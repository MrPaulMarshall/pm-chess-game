package com.pmarshall.chessgame.client.controller;

import com.pmarshall.chessgame.client.App;
import com.pmarshall.chessgame.client.FXMLUtils;
import com.pmarshall.chessgame.model.properties.Color;
import com.pmarshall.chessgame.model.properties.PieceType;
import com.pmarshall.chessgame.model.service.Game;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
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

    private LocalGameController(Game game) {
        this.game = game;
    }

    /**
     * Initializes game and displays view
     */
    public static void initRootLayout() {
        Optional<Game> loadedGameService = ServiceLoader.load(Game.class).findFirst();
        if (loadedGameService.isEmpty()) {
            log.error("Game service was not provided, cannot use local mode");
            return;
        }
        Game game = loadedGameService.get();

        LocalGameController controller = new LocalGameController(game);
        Parent root = FXMLUtils.load(controller, "/view/game_screen.fxml");
        App.primaryStage().setScene(new Scene(root));
    }

    @FXML
    private void initialize() {
        chatText.setDisable(true);
        chatInputField.setDisable(true);

        createBoardGrid(true);
        upperPlayerNameLabel.setText("BLACK");
        upperPlayerImageView.setImage(imageProvider.getImage(PieceType.KING, Color.BLACK));
        lowerPlayerNameLabel.setText("WHITE");
        lowerPlayerImageView.setImage(imageProvider.getImage(PieceType.KING, Color.WHITE));
        refreshBoard(game.currentPlayer(), game.getBoardWithPieces());
    }

    @FXML
    private void handleLocalPlayerChatInput(KeyEvent ignored) {
        // unreachable
        throw new IllegalStateException();
    }

    @FXML
    private void handleSurrenderAction(ActionEvent ignored) {
        endGame(game.currentPlayer().next(), "surrender");
    }

    @FXML
    private void handleDrawAction(ActionEvent ignored) {
        endGame(null, "agreement");
    }

    @Override
    protected boolean arePlayersPiecesDisabled(Color player) {
        return false;
    }
}
