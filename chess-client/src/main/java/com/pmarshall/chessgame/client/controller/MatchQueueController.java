package com.pmarshall.chessgame.client.controller;

import com.pmarshall.chessgame.api.Message;
import com.pmarshall.chessgame.api.Parser;
import com.pmarshall.chessgame.api.lobby.LogIn;
import com.pmarshall.chessgame.api.lobby.MatchFound;
import com.pmarshall.chessgame.api.lobby.Ping;
import com.pmarshall.chessgame.client.App;
import com.pmarshall.chessgame.client.FXMLUtils;
import com.pmarshall.chessgame.client.remote.ServerConnection;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class MatchQueueController {

    private static final Logger log = LoggerFactory.getLogger(MatchQueueController.class);

    private static String playerName = null;

    private ServerConnection connection;
    private Thread waitingThread;
    private volatile boolean cancelled;

    public static void initRootLayout() {
        MatchQueueController controller = new MatchQueueController();
        Parent root = FXMLUtils.load(controller, "/view/match_queue_screen.fxml");

        if (playerName != null) {
            resumeInitRootLayout(root, controller);
        } else {
            UserNameDialogController.askUserForName(userInput -> {
                log.info("User provided name: {}", userInput);
                setPlayerName(userInput);
                resumeInitRootLayout(root, controller);
            });
        }
    }

    private static void resumeInitRootLayout(Parent rootLayout, MatchQueueController controller) {
        ServerConnection connection;
        try {
            connection = connectToServer();
            logInToServer(connection.out(), playerName);
        } catch (IOException e) {
            log.error("Could not connect to the server", e);
            controller.handleCancelAction();
            return;
        }

        controller.connection = connection;

        App.primaryStage().setScene(new Scene(rootLayout));

        controller.waitingThread = new Thread(() -> {
            try {
                MatchFound matchFound = waitForOpponentMatch(connection.in());
                Platform.runLater(() -> controller.initGame(playerName, matchFound));
            } catch (IOException e) {
                Platform.runLater(controller::handleCancelAction);
                log.warn("Could not connect to the server and initialize game", e);
            }
        });
        controller.waitingThread.start();
    }

    private static void setPlayerName(String name) {
        playerName = name;
    }

    private static ServerConnection connectToServer() throws IOException {
        InetSocketAddress address = App.serverAddress();
        if (address == null || address.isUnresolved())
            throw new IOException("Cannot determine server address");

        Socket socket = new Socket();
        socket.connect(address, 2_000);
        InputStream in = socket.getInputStream();
        OutputStream out = socket.getOutputStream();

        return new ServerConnection(socket, in, out);
    }

    private static void logInToServer(OutputStream out, String name) throws IOException {
        LogIn message = new LogIn(name);
        byte[] messageBuffer = Parser.serialize(message);
        byte[] headerBuffer = Parser.serializeLength(messageBuffer.length);

        out.write(headerBuffer);
        out.write(messageBuffer);
    }

    private static MatchFound waitForOpponentMatch(InputStream in) throws IOException {
        Message message;
        do {
            byte[] headerBuffer = in.readNBytes(2);
            int length = Parser.deserializeLength(headerBuffer);
            byte[] messageBuffer = in.readNBytes(length);

            message = Parser.deserialize(messageBuffer, length);
        } while (message instanceof Ping);

        if (!(message instanceof MatchFound))
            throw new IOException("Cannot initialize game due to unexpected message from server: " + message);

        return (MatchFound) message;
    }

    @FXML
    private void handleCancelAction() {
        if (cancelled)
            return;

        cancelled = true;

        if (connection != null) {
            try {
                connection.socket().close();
            } catch (IOException e) {
                log.warn("Exception while closing the socket", e);
            }
        }

        MenuController.initRootLayout();
    }

    private void initGame(String playerId, MatchFound matchFound) {
        RemoteGameController.initRootLayout(connection, playerId, matchFound);
    }
}
