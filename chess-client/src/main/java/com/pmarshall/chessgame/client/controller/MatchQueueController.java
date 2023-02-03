package com.pmarshall.chessgame.client.controller;

import com.pmarshall.chessgame.api.Message;
import com.pmarshall.chessgame.api.Parser;
import com.pmarshall.chessgame.api.lobby.AssignId;
import com.pmarshall.chessgame.api.lobby.MatchFound;
import com.pmarshall.chessgame.client.App;
import com.pmarshall.chessgame.client.remote.ServerConnection;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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

    private Stage primaryStage;
    private ServerConnection connection;
    private Thread waitingThread;
    private volatile boolean cancelled;

    public static void initRootLayout(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(MatchQueueController.class.getResource("/view/match_queue_screen.fxml"));

        BorderPane rootLayout = loader.load();
        Scene scene = new Scene(rootLayout);

        MatchQueueController controller = loader.getController();
        ServerConnection connection = connectToServer();
        controller.injectDependencies(primaryStage, connection);

        primaryStage.setTitle("Chess board");
        primaryStage.setScene(scene);
        primaryStage.show();

        controller.waitingThread = new Thread(() -> {
            try {
                String id = waitForIdAssignment(connection.in());
                MatchFound matchFound = waitForOpponentMatch(connection.in());
                Platform.runLater(() -> controller.initGame(id, matchFound));
            } catch (IOException e) {
                Platform.runLater(controller::handleCancelAction);
                log.warn("Could not connect to the server and initialize game", e);
            }
        });
        controller.waitingThread.start();
    }

    private static ServerConnection connectToServer() throws IOException {
        InetSocketAddress address = App.getServerAddress();
        if (address == null || address.isUnresolved())
            throw new IOException("Cannot determine server address");

        Socket socket = new Socket();
        socket.connect(address);
        InputStream in = socket.getInputStream();
        OutputStream out = socket.getOutputStream();

        return new ServerConnection(socket, in, out);
    }

    private static String waitForIdAssignment(InputStream in) throws IOException {
        byte[] headerBuffer = in.readNBytes(2);
        int length = Parser.deserializeLength(headerBuffer);
        byte[] messageBuffer = in.readNBytes(length);

        // if the message is different that AssignId, then the contract is broken and client cannot continue
        AssignId message = (AssignId) Parser.deserialize(messageBuffer, length);
        return message.id();
    }

    private static MatchFound waitForOpponentMatch(InputStream in) throws IOException {
        byte[] headerBuffer = in.readNBytes(2);
        int length = Parser.deserializeLength(headerBuffer);
        byte[] messageBuffer = in.readNBytes(length);

        // if the message is different that MatchFound, then the contract is broken and client cannot continue
        Message message = Parser.deserialize(messageBuffer, length);
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
        try {
            connection.socket().close();
        } catch (IOException e) {
            log.warn("Exception while closing the socket", e);
        }

        try {
            MenuController.initRootLayout(primaryStage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void initGame(String playerId, MatchFound matchFound) {
        try {
            RemoteGameController.initRootLayout(primaryStage, connection, playerId, matchFound);
        } catch (IOException e) {
            log.warn("Could not init board after match has been found", e);
            handleCancelAction();
        }
    }
}
