package com.pmarshall.chessgame.server.threads;

import com.pmarshall.chessgame.model.service.Game;
import com.pmarshall.chessgame.server.MatchRegister;
import com.pmarshall.chessgame.server.PlayerConnection;
import com.pmarshall.chessgame.api.Message;
import com.pmarshall.chessgame.api.endrequest.DrawProposition;
import com.pmarshall.chessgame.api.endrequest.DrawResponse;
import com.pmarshall.chessgame.api.lobby.MatchFound;
import com.pmarshall.chessgame.api.move.Move;
import com.pmarshall.chessgame.api.move.OpponentMoved;
import com.pmarshall.chessgame.api.outcome.GameFinished;
import com.pmarshall.chessgame.model.properties.Color;
import com.pmarshall.chessgame.model.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

import static com.pmarshall.chessgame.model.properties.Color.BLACK;
import static com.pmarshall.chessgame.model.properties.Color.WHITE;

/**
 * Represents the game session between two players, and handles all relevant data,
 * like connection sockets, colors, chessboard, message queues etc.
 * <p>
 * As a thread, it enforces chess rules and synchronizes state of the game between players.
 * It's also responsible for cleaning up resources at JVM shutdown or the end of the match.
 */
public class Master extends Thread {

    private static final Logger log = LoggerFactory.getLogger(Master.class);
    private static final Random RANDOM = new Random();

    /* Reference to register */
    private final MatchRegister register;
    private final int matchId;

    /* State of the game */
    private final Game game;
    private Color drawProponent;

    /* Players metadata */
    private final Map<Color, PlayerConnection> players;
    private final ConcurrentSkipListSet<Color> deadConnections = new ConcurrentSkipListSet<>();
    private final Map<Color, Reader> readerThreads;
    private final Map<Color, Writer> writerThreads;

    /* Synchronization and communication tools */
    private final Semaphore semaphore = new Semaphore(0);
    private final BlockingQueue<Color> surrenderQueue = new ArrayBlockingQueue<>(1);
    private final BlockingQueue<Pair<Color, Message>> masterQueue = new LinkedBlockingQueue<>();

    public Master(PlayerConnection p1, PlayerConnection p2, int matchId, MatchRegister register) {
        super("Master-" + p1.id() + "-" + p2.id());

        this.register = register;
        this.matchId = matchId;

        /* Decide which player will be white */
        int i = RANDOM.nextInt(2);
        players = Map.of(
                Color.values()[  i], p1,
                Color.values()[1-i], p2
        );

        /* Initialize data and threads for players */
        writerThreads = Map.of(
                WHITE, new Writer(WHITE, players.get(WHITE).id(), players.get(WHITE).out(), this),
                BLACK, new Writer(BLACK, players.get(BLACK).id(), players.get(BLACK).out(), this)
        );
        readerThreads = Map.of(
                WHITE, new Reader(WHITE, players.get(WHITE).id(), players.get(WHITE).in(), this, writerThreads.get(BLACK)),
                BLACK, new Reader(BLACK, players.get(BLACK).id(), players.get(BLACK).in(), this, writerThreads.get(WHITE))
        );

        /* Initialize logical representation of the game */
        game = ServiceLoader.load(Game.class).findFirst().orElseThrow();
    }

    @Override
    public void run() {
        log.info("The game between {} as {} and {} as {} is starting",
                players.get(WHITE).id(), WHITE, players.get(BLACK).id(), BLACK);

        /* Prepare init messages */
        try {
            writerThreads.get(BLACK).pushMessage(
                    new MatchFound(BLACK, players.get(WHITE).name(), Collections.emptyList()));
            writerThreads.get(WHITE).pushMessage(
                    new MatchFound(WHITE, players.get(BLACK).name(), game.legalMoves()));
        } catch (InterruptedException ignored) {
            log.warn("Thread {} interrupted before the game could start", Thread.currentThread().getName());
            return;
        }

        /* start the threads */
        readerThreads.values().forEach(Thread::start);
        writerThreads.values().forEach(Thread::start);

        /* run the main loop */
        while (!Thread.interrupted()) {
            try {
                // is any message available?
                semaphore.acquire();

                // check if any connection is down
                Set<Color> deadConnectionsCopy = deadConnections.clone();
                if (!deadConnectionsCopy.isEmpty()) {
                    cleanUpAtDeadConnection(deadConnectionsCopy);
                    joinWorkerThreads();
                    break;
                }

                // check if player surrendered
                Color surrendered = surrenderQueue.poll();
                if (surrendered != null) {
                    terminateGame(Map.of(
                            surrendered, new GameFinished(GameFinished.Type.DEFEAT, "surrender"),
                            surrendered.next(), new GameFinished(GameFinished.Type.VICTORY, "surrender")
                    ));
                    break;
                }

                Pair<Color, Message> messageFromPlayer = masterQueue.take();
                Color sender = messageFromPlayer.left();
                Message message = messageFromPlayer.right();

                if (message instanceof DrawProposition) {
                    if (drawProponent == null) {
                        drawProponent = sender;
                        writerThreads.get(sender.next()).pushMessage(message);
                    } else {
                        log.warn("Cannot receive draw proposition from {}, because {} has already proposed it",
                                sender, drawProponent);
                    }
                    continue;
                }

                if (message instanceof DrawResponse drawResponse) {
                    boolean gameEnded = handleDrawResponse(sender, drawResponse.accepted());
                    if (gameEnded)
                        break;
                    continue;
                }

                // check if message was move
                if (message instanceof Move move) {
                    boolean gameEnded = handleMoveRequest(sender, move);
                    if (gameEnded)
                        break;
                    continue;
                }

                // cannot handle the message
                log.warn("Unknown message {}", message);
            } catch (InterruptedException e) {
                log.warn("Thread {} was interrupted", Thread.currentThread().getName());
                readerThreads.values().forEach(Thread::interrupt);
                readerThreads.values().forEach(Thread::interrupt);
                joinWorkerThreads();
            }
        }

        log.info("The game between {} as {} and {} as {} has ended",
                players.get(WHITE).id(), WHITE, players.get(BLACK).id(), BLACK);

        try {
            register.notifyGameEnded(matchId);
        } catch (InterruptedException e) {
            log.warn("Thread {} interrupted during reporting the end of the game", Thread.currentThread().getName(), e);
        }
    }

    private void terminateGame(Map<Color, GameFinished> messages) {
        // stop listening for messages
        readerThreads.values().forEach(Thread::interrupt);

        // notify client about the end of the game
        messages.forEach((color, outcome) -> {
            try {
                writerThreads.get(color).pushGameOutcome(outcome);
            } catch (InterruptedException ex) {
                log.error("Could not publish ending message to {}", color);
            }
        });

        // join worker threads
        joinWorkerThreads();
    }

    private void cleanUpAtDeadConnection(Set<Color> deadConnections) throws InterruptedException {
        readerThreads.values().forEach(Thread::interrupt);

        if (deadConnections.size() == 1) {
            Color color = deadConnections.iterator().next();

            // Inform the active player that the opponent had quit
            writerThreads.get(color.next()).pushGameOutcome(
                    new GameFinished(GameFinished.Type.VICTORY, "surrender"));
        } // else there is no one to notify

        // Kill the Writer threads operating on broken connections
        for (Color color : deadConnections) {
            writerThreads.get(color).interrupt();
            try {
                players.get(color).out().close();
            } catch (IOException e) {
                log.warn("Could not close socket of player {}", players.get(color).id(), e);
            }
        }
    }

    /**
     * @return true if game has ended, false if it continues
     */
    private boolean handleDrawResponse(Color sender, boolean drawAccepted) throws InterruptedException {
        if (drawProponent == null) {
            log.warn("{} cannot respond to draw proposition, because there was none", sender);
            return false;
        }

        if (drawProponent == sender) {
            log.warn("{} cannot respond to draw proposition made by himself", sender);
            return false;
        }

        if (drawAccepted) {
            terminateGame(Map.of(
                    sender, new GameFinished(GameFinished.Type.DRAW, "agreement"),
                    sender.next(), new GameFinished(GameFinished.Type.DRAW, "agreement")
            ));
            return true;
        } else {
            drawProponent = null;
            return false;
        }
    }

    /**
     * @return true if the game has ended, false if it continues
     */
    private boolean handleMoveRequest(Color sender, Move move) throws InterruptedException {
        if (sender != game.currentPlayer()) {
            log.warn("Cannot push moves when because it's {} turn", sender.next());
            return false;
        }

        boolean legalMove;
        if (move.promotion() != null) {
            legalMove = game.executeMove(move.from(), move.to(), move.promotion());
        } else {
            legalMove = game.executeMove(move.from(), move.to());
        }

        if (!legalMove) {
            log.warn("Move {} is not legal", move);
            return false;
        }

        // check if game ended
        Pair<Color, String> gameResult = game.outcome();
        if (gameResult != null) {
            if (gameResult.left() == null) {
                GameFinished drawOutcome = new GameFinished(GameFinished.Type.DRAW, "stalemate");
                terminateGame(Map.of(WHITE, drawOutcome, BLACK, drawOutcome));
            } else {
                Color winner = gameResult.left();
                terminateGame(Map.of(
                        winner, new GameFinished(GameFinished.Type.VICTORY, "checkmate"),
                        winner.next(), new GameFinished(GameFinished.Type.DEFEAT, "checkmate")
                ));
            }

            return true;
        }

        OpponentMoved opponentNotification = new OpponentMoved(game.lastMove(), game.legalMoves());
        writerThreads.get(sender.next()).pushMessage(opponentNotification);

        return false;
    }

    private void joinWorkerThreads() {
        // join worker threads
        readerThreads.forEach((color, reader) -> {
            try {
                reader.join();
            } catch (InterruptedException ex) {
                log.error("Error during joining {} Reader", color, ex);
            }
        });
        writerThreads.forEach((color, writer) -> {
            try {
                writer.join();
            } catch (InterruptedException ex) {
                log.error("Error during joining {} Writer", color, ex);
            }
        });
    }

    public void pushMessage(Color color, Message msg) throws InterruptedException {
        masterQueue.put(Pair.of(color, msg));
        semaphore.release();
    }

    public void notifySurrender(Color color) throws InterruptedException {
        surrenderQueue.put(color);
        semaphore.release();
    }

    public void notifyConnectionLost(Color color) {
        deadConnections.add(color);
        semaphore.release();
    }

    public PlayerConnection[] getPlayers() {
        return new PlayerConnection[]{players.get(WHITE), players.get(BLACK)};
    }
}
