package com.pmarshall.chessgame.client.controller;

import com.pmarshall.chessgame.api.Message;
import com.pmarshall.chessgame.api.Parser;
import com.pmarshall.chessgame.api.lobby.LogIn;
import com.pmarshall.chessgame.api.lobby.MatchFound;
import com.pmarshall.chessgame.api.lobby.Ping;
import com.pmarshall.chessgame.client.App;
import com.pmarshall.chessgame.client.remote.ServerConnection;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
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

    private Stage primaryStage;
    private ServerConnection connection;
    private Thread waitingThread;
    private volatile boolean cancelled;

    public static void initRootLayout(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(MatchQueueController.class.getResource("/view/match_queue_screen.fxml"));
        BorderPane rootLayout = loader.load();
        MatchQueueController controller = loader.getController();

        if (playerName != null) {
            resumeInitRootLayout(primaryStage, rootLayout, controller);
        } else {
            UserNameDialogController.askUserForName(primaryStage, userInput -> {
                log.info("User provided name: {}", userInput);
                setPlayerName(userInput);
                resumeInitRootLayout(primaryStage, rootLayout, controller);
            });
        }
    }

    private static void resumeInitRootLayout(Stage primaryStage,
                                             Parent rootLayout,
                                             MatchQueueController controller) {
        ServerConnection connection;
        try {
            connection = connectToServer();
            logInToServer(connection.out(), playerName);
        } catch (IOException e) {
            log.error("Could not connect to the server", e);
            controller.injectDependencies(primaryStage, null);
            controller.handleCancelAction();
            return;
        }

        controller.injectDependencies(primaryStage, connection);

        Scene scene = new Scene(rootLayout);
        primaryStage.setTitle("Chess board");
        primaryStage.setScene(scene);
        primaryStage.show();

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
        InetSocketAddress address = App.getServerAddress();
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

    private void injectDependencies(Stage primaryStage, ServerConnection connection) {
        this.primaryStage = primaryStage;
        this.connection = connection;
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

        MenuController.initRootLayout(primaryStage);
    }

    private void initGame(String playerId, MatchFound matchFound) {
        RemoteGameController.initRootLayout(primaryStage, connection, playerId, matchFound);
    }
}
