package com.marshall.chessgame.server;

public class Server {

    public static void main(String[] args) {
        Runtime.getRuntime().addShutdownHook(new CleanUpHook());
        initializeServer();
        acceptClientConnections();
    }

    private static void initializeServer() {
        // TODO: initialize the server: collections of player-handlers, ServerSocket etc.
    }

    private static void acceptClientConnections() {
        while (true) {
            // TODO: accept incoming clients and startup their sessions
        }
    }

    /**
     * Shutdown hook that will terminate open games, interrupt threads, close opened sockets etc.
     */
    private static class CleanUpHook extends Thread {
        @Override
        public void run() {
            // TODO: implement me :)
        }
    }
}
