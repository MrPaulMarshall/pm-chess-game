package com.pmarshall.chessgame.client.controller;

import com.pmarshall.chessgame.api.lobby.MatchFound;
import com.pmarshall.chessgame.api.outcome.GameFinished;
import com.pmarshall.chessgame.client.App;
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

    private RemoteGameController(RemoteGameProxy game, String name, MatchFound matchFound) {
        this.game = game;
        this.serverProxy = game;
        this.namesOfPlayers = Map.of(matchFound.color(), name, matchFound.color().next(), matchFound.opponentName());
    }

    /**
     * Initializes game and displays view
     */
    public static void initRootLayout(ServerConnection connection, String name, MatchFound matchFound) {
        RemoteGameController controller = new RemoteGameController(null, name, matchFound);
        RemoteGameProxy proxy = new RemoteGameProxy(controller, connection, matchFound);
        controller.game = proxy;
        controller.serverProxy = proxy;

        Parent root = FXMLUtils.load(controller, "/view/remote_game_screen.fxml");
        App.primaryStage().setScene(new Scene(root));
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
        DrawRequestController.initRootLayout(this);
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

    /**
     * Disables board, creates dialog with result, and closes the program
     * @param outcome result of the match for the local player
     * @param reason event that ended the game; should be insertable into "Ended by $reason"
     */
    public void endGame(GameFinished.Type outcome, String reason) {
        // TODO: disable 'physical' buttons instead (i.e. disable GUI elements)
        if (gameEnded) return;
        gameEnded = true;

        String result = switch (outcome) {
            case VICTORY -> "You've won";
            case DEFEAT -> "You've lost";
            case DRAW -> "A draw";
        };

        GameEndedController.initRootLayout(result + " by " + reason);
    }

    @Override
    protected boolean arePlayersPiecesDisabled(Color player) {
        return player != serverProxy.localPlayer();
    }
}
