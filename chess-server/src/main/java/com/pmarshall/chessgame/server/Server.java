package com.pmarshall.chessgame.server;

import com.pmarshall.chessgame.api.Parser;
import com.pmarshall.chessgame.api.lobby.AssignId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private static final Logger log = LoggerFactory.getLogger(Server.class);

    private static final Thread mainThread = Thread.currentThread();
    private static ServerSocket serverSocket;
    private static MatchRegister register;

    public static void main(String[] args) throws IOException {
        Runtime.getRuntime().addShutdownHook(new CleanUpHook());

        initializeServer();
        acceptClientConnections(serverSocket);
    }

    private static void initializeServer() throws IOException {
        register = new MatchRegister();
        serverSocket = new ServerSocket(21370);
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
            AssignId assignId = new AssignId(id);

            OutputStream out = socket.getOutputStream();
            byte[] msgBytes = Parser.serialize(assignId);
            byte[] lengthHeader = Parser.serializeLength(msgBytes.length);
            out.write(lengthHeader);
            out.write(msgBytes);

            register.registerNewPlayer(id, socket);
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
