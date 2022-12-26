package com.marshall.chessgame.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private static final Thread mainThread = Thread.currentThread();

    public static void main(String[] args) throws IOException {
        Runtime.getRuntime().addShutdownHook(new CleanUpHook());

        final ServerSocket serverSocket = initializeServer();
        try (serverSocket) {
            acceptClientConnections(serverSocket);
        }
    }

    private static ServerSocket initializeServer() throws IOException {
        // TODO: initialize the server: collections of player-handlers, ServerSocket etc.
        return new ServerSocket(0);
    }

    private static void acceptClientConnections(ServerSocket serverSocket) throws IOException {
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
            mainThread.interrupt();
        }
    }
}
