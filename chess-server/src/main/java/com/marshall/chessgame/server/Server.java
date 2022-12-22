package com.marshall.chessgame.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private static ServerSocket serverSocket;

    public static void main(String[] args) throws IOException {
        Runtime.getRuntime().addShutdownHook(new CleanUpHook());
        initializeServer();
        acceptClientConnections();
    }

    private static void initializeServer() throws IOException {
        // TODO: initialize the server: collections of player-handlers, ServerSocket etc.
        serverSocket = new ServerSocket(0);
    }

    private static void acceptClientConnections() throws IOException {
        while (true) {
            Socket socket = serverSocket.accept();
            initiateClientSession(socket);
        }
    }

    private static void initiateClientSession(Socket socket) throws IOException {
        // TODO: implement me :)
        socket.close();
    }

    /**
     * Shutdown hook that will terminate open games, interrupt threads, close opened sockets etc.
     */
    private static class CleanUpHook extends Thread {
        @Override
        public void run() {
            // TODO: implement me :)
            try {
                serverSocket.close();
            } catch (IOException ignored) {
            }
        }
    }
}
