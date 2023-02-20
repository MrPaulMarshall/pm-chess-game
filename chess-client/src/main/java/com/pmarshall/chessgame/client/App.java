package com.pmarshall.chessgame.client;

import com.pmarshall.chessgame.client.controller.MenuController;
import javafx.application.Application;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * Main class that starts lifecycle of the game
 */
public class App extends Application {

    private static final Logger log = LoggerFactory.getLogger(App.class);

    private static InetSocketAddress serverAddress;

    public static void main(String[] args) {
        log.debug("Launched from {}", App.class.getModule().isNamed() ? "modulepath" : "classpath");

        try {
            String serverIp = args.length >= 1 ? args[0] : "127.0.0.1";
            int port = args.length >= 2 ? Integer.parseInt(args[1]) : 21370;
            serverAddress = new InetSocketAddress(serverIp, port);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid network address", e);
        }

        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Chess game by Paweł Marszał");
        MenuController.initRootLayout(primaryStage);
    }

    public static InetSocketAddress getServerAddress() {
        return serverAddress;
    }

}
