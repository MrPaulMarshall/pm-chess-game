package com.pmarshall.chessgame.client;

import com.pmarshall.chessgame.client.controller.MenuController;
import javafx.application.Application;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Arrays;

/**
 * Main class that starts lifecycle of the game
 */
public class App extends Application {

    private static final Logger log = LoggerFactory.getLogger(App.class);

    private static InetSocketAddress serverAddress;

    private static Stage primaryStage;

    public static void main(String[] args) {
        log.debug("Launched from {}", App.class.getModule().isNamed() ? "modulepath" : "classpath");

        if (args.length > 1) {
            log.warn("Too many command line arguments: {}", Arrays.toString(args));
            System.exit(1);
        }

        try {
            String[] ipPortTuple = (args.length == 1 ? args[0] : "localhost:21370").split(":");
            String ip = ipPortTuple[0];
            int port = ipPortTuple.length == 2 ? Integer.parseInt(ipPortTuple[1]) : 21370;
            serverAddress = new InetSocketAddress(ip, port);
        } catch (Exception e) {
            log.warn("Invalid network address", e);
            System.exit(1);
        }

        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        App.primaryStage = primaryStage;
        primaryStage.setTitle("Marszal Chess FX");
        MenuController.initRootLayout();
        primaryStage.show();
    }

    public static InetSocketAddress serverAddress() {
        return serverAddress;
    }

    public static Stage primaryStage() {
        return primaryStage;
    }
}
