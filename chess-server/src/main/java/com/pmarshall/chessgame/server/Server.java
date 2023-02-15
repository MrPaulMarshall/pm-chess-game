package com.pmarshall.chessgame.server;

import com.pmarshall.chessgame.api.Parser;
import com.pmarshall.chessgame.api.lobby.LogIn;
import com.pmarshall.chessgame.model.service.Game;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ServiceLoader;

public class Server {

    private static final Logger log = LoggerFactory.getLogger(Server.class);
    private static final int DEFAULT_PORT = 21370;

    private static final Thread mainThread = Thread.currentThread();
    private static ServerSocket serverSocket;
    private static MatchRegister register;

    public static void main(String[] args) throws IOException {
        log.debug("Launched from {}", Server.class.getModule().isNamed() ? "modulepath" : "classpath");

        if (ServiceLoader.load(Game.class).stream().findFirst().isEmpty()) {
            log.error("Game service was not provided");
            System.exit(1);
        }

        int port = args.length >= 1 ? parsePort(args[0]) : DEFAULT_PORT;
        Runtime.getRuntime().addShutdownHook(new CleanUpHook());

        initializeServer(port);
        acceptClientConnections(serverSocket);
    }

    private static int parsePort(String arg) {
        int port;
        try {
            port = Integer.parseInt(arg);
        } catch (NumberFormatException e) {
            log.warn("Cannot parse port number: {}", arg, e);
            return DEFAULT_PORT;
        }

        if (1024 <= port && port <= 65535) {
            return port;
        } else {
            log.warn("Port {} is outside allowed range, defaults to {}", port, DEFAULT_PORT);
            return DEFAULT_PORT;
        }
    }

    private static void initializeServer(int port) throws IOException {
        register = new MatchRegister();
        serverSocket = new ServerSocket(port);
        register.start();
    }

    private static void acceptClientConnections(final ServerSocket serverSocket) throws IOException {
        log.info("Server starts accepting client connections");
        try (serverSocket) {
            while (!Thread.interrupted()) {
                try {
                    Socket socket = serverSocket.accept();
                    registerNewConnection(socket);
                } catch (IOException | InterruptedException e) {
                    log.error("Server encountered error", e);
                    register.interrupt();
                    break;
                }
            }
        }
    }

    private static void registerNewConnection(Socket socket) throws InterruptedException {
        try {
            String id = register.generateNewId();

            InputStream in = socket.getInputStream();
            byte[] lengthHeader = in.readNBytes(2);
            int length = Parser.deserializeLength(lengthHeader);
            byte[] messageBuffer = in.readNBytes(length);
            LogIn message = (LogIn) Parser.deserialize(messageBuffer, length);

            register.registerNewPlayer(id, message.name(), socket);
        } catch (IOException e) {
            log.error("Could not notify new player about accepting him", e);
        }
    }

    /**
     * Shutdown hook that will terminate open games, interrupt threads, close opened sockets etc.
     */
    private static class CleanUpHook extends Thread {
        @Override
        public void run() {
            mainThread.interrupt();

            try {
                serverSocket.close();
            } catch (IOException e) {
                log.error("Could not close server socket", e);
            }

            register.interrupt();
        }
    }
}
