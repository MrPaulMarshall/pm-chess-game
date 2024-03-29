package com.pmarshall.chessgame.server;

import com.pmarshall.chessgame.api.Parser;
import com.pmarshall.chessgame.api.lobby.Ping;
import com.pmarshall.chessgame.model.util.Pair;
import com.pmarshall.chessgame.server.threads.Master;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;

public class MatchRegister extends Thread {

    private static final Logger log = LoggerFactory.getLogger(MatchRegister.class);

    private final Map<String, Pair<Socket, String>> liveConnections = new ConcurrentHashMap<>();

    private final Map<Integer, Master> activeGames = new HashMap<>();

    private final BlockingQueue<String> freePlayers = new LinkedBlockingQueue<>();
    private final BlockingQueue<Integer> finishedGameIds = new LinkedBlockingQueue<>();

    private final Semaphore semaphore = new Semaphore(0);

    public MatchRegister() {
        super("MatchRegister");
    }

    @Override
    public void run() {
        int gameIdSeq = 0;

        while (!Thread.interrupted()) {
            try {
                semaphore.acquire();

                Integer gameId = finishedGameIds.poll();
                if (gameId != null) {
                    Master finishedGame = activeGames.remove(gameId);
                    PlayerConnection[] players = finishedGame.getPlayers();
                    // freePlayers.put(players[0].id());
                    // freePlayers.put(players[1].id());
                    // TODO: reuse connections when the same policy is used on the client side
                    //       alternatively, define keep-alive protocol to detect dead connections
                    try {
                        liveConnections.get(players[0].id()).left().close();
                    } catch (IOException ignored) {}
                    try {
                        liveConnections.get(players[1].id()).left().close();
                    } catch (IOException ignored) {}
                }

                // start as many games as possible
                while (freePlayers.size() >= 2) {
                    PlayerConnection[] gameMatch = new PlayerConnection[2];
                    int count = 0;
                    while (count < 2 && count + freePlayers.size() >= 2) {
                        String playerId = freePlayers.take();
                        Pair<Socket, String> connection = liveConnections.get(playerId);
                        Socket socket = connection.left();
                        String name = connection.right();

                        try {
                            OutputStream out = socket.getOutputStream();
                            byte[] messageBuffer = Parser.serialize(new Ping());
                            byte[] lengthBuffer = Parser.serializeLength(messageBuffer.length);
                            out.write(lengthBuffer);
                            out.write(messageBuffer);

                            gameMatch[count] = new PlayerConnection(playerId, name, socket.getInputStream(), out);
                            count++;
                        } catch (IOException e) {
                            log.error("Connection to player {} is dead", playerId, e);
                            try {
                                socket.close();
                            } catch (IOException ignored) {}
                            liveConnections.remove(playerId);
                        }
                    }

                    // initiate new game or return single player to waiting queue
                    if (count == 2) {
                        Master master = new Master(gameMatch[0], gameMatch[1], gameIdSeq, this);
                        activeGames.put(gameIdSeq, master);
                        gameIdSeq++;
                        master.start();
                    } else if (count == 1) {
                        freePlayers.put(gameMatch[0].id());
                    }
                }
            } catch (InterruptedException e) {
                log.warn("Thread {} was interrupted", Thread.currentThread().getName());
            }
        }

        terminate();
        log.info("Thread {} is shutting down", Thread.currentThread().getName());
    }

    private void terminate() {
        activeGames.values().forEach(Thread::interrupt);

        liveConnections.forEach((id, connection) -> {
            try {
                connection.left().close();
            } catch (IOException e) {
                log.error("Could not close socket of player {}", id, e);
            }
        });

        activeGames.values().forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                log.error("Could not join thread {}", t.getName(), e);
            }
        });
    }

    public boolean registerNewPlayer(Socket socket, String name) {
        Pair<Socket, String> previousValue;
        String id;
        // generate ids until unique one is found (and insert is successful)
        do {
            id = Long.toHexString(Double.doubleToLongBits(Math.random()));
            previousValue = liveConnections.putIfAbsent(id, Pair.of(socket, name));
        } while (previousValue != null);

        try {
            freePlayers.put(id);
        } catch (InterruptedException e) {
            liveConnections.remove(id);
            return false;
        }

        semaphore.release();
        return true;
    }

    public void notifyGameEnded(int gameId) throws InterruptedException {
        finishedGameIds.put(gameId);
        semaphore.release();
    }
}
