package com.pmarshall.chessgame.client.controller;

import com.pmarshall.chessgame.api.lobby.MatchFound;
import com.pmarshall.chessgame.client.FXMLUtils;
import com.pmarshall.chessgame.client.remote.ServerConnection;
import com.pmarshall.chessgame.model.properties.Color;
import com.pmarshall.chessgame.client.remote.RemoteGameProxy;
import com.pmarshall.chessgame.client.remote.ServerProxy;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Map;

/**
 * @author Paweł Marszał
 *
 * Controller of game view in remote mode
 */
public class RemoteGameController extends GameControllerBase {

    @FXML
    private TextArea chatText;

    @FXML
    private TextField chatInputField;

    private ServerProxy serverProxy;

    private final Map<Color, String> namesOfPlayers;

    private RemoteGameController(Stage primaryStage, RemoteGameProxy game, String name, MatchFound matchFound) {
        this.primaryStage = primaryStage;
        this.game = game;
        this.serverProxy = game;
        this.namesOfPlayers = Map.of(matchFound.color(), name, matchFound.color().next(), matchFound.opponentName());
    }

    /**
     * Initializes game and displays view
     */
    public static void initRootLayout(Stage primaryStage, ServerConnection connection, String name, MatchFound matchFound) {
        RemoteGameController controller = new RemoteGameController(primaryStage, null, name, matchFound);
        RemoteGameProxy proxy = new RemoteGameProxy(controller, connection, matchFound);
        controller.game = proxy;
        controller.serverProxy = proxy;

        Parent root = FXMLUtils.load(controller, "/view/remote_game_screen.fxml");

        Scene scene = new Scene(root);
        primaryStage.setTitle("Chess board");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @FXML
    private void initialize() {
        createBoardGrid(serverProxy.localPlayer() == Color.WHITE);
        refreshBoard(game.currentPlayer(), game.getBoardWithPieces());
    }

    @FXML
    private void handleSurrenderAction(ActionEvent ignored) throws InterruptedException {
        serverProxy.surrender();
    }

    @FXML
    private void handleDrawAction(ActionEvent ignored) throws InterruptedException {
        serverProxy.proposeDraw();
    }

    @FXML
    private void handleLocalPlayerChatInput(KeyEvent event) throws InterruptedException {
        if (event.getCode() == KeyCode.ENTER) {
            String userInput = chatInputField.getText();
            chatInputField.setText("");
            appendToChat(serverProxy.localPlayer(), userInput);
            serverProxy.pushChatMessage(userInput);
        }
    }

    public void showDrawRequestedWindow() {
        // TODO: disable interacting with the board
        try {
            DrawRequestController.initRootLayout(primaryStage, this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void acceptDraw() {
        try {
            serverProxy.acceptDraw();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void rejectDraw() {
        try {
            serverProxy.rejectDraw();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void appendToChat(Color player, String message) {
        chatText.setEditable(true);
        chatText.appendText(namesOfPlayers.get(player) + ": " + message + "\n");
        chatText.setEditable(false);
    }

    @Override
    protected boolean arePlayersPiecesDisabled(Color player) {
        return player != serverProxy.localPlayer();
    }
}
