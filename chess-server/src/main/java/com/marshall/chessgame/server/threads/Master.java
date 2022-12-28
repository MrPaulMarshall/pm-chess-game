package com.marshall.chessgame.server.threads;

import com.marshall.chessgame.server.domain.ConnectionDeadException;
import com.marshall.chessgame.server.domain.PlayerConnection;
import com.pmarshall.chessgame.api.Message;
import com.pmarshall.chessgame.api.endrequest.DrawRequest;
import com.pmarshall.chessgame.api.move.request.Move;
import com.pmarshall.chessgame.api.move.request.MoveRequest;
import com.pmarshall.chessgame.api.move.request.PromotionDecision;
import com.pmarshall.chessgame.api.move.response.MoveAccepted;
import com.pmarshall.chessgame.api.move.response.MoveRejected;
import com.pmarshall.chessgame.api.outcome.GameOutcome;
import com.pmarshall.chessgame.model.game.Game;
import com.pmarshall.chessgame.model.properties.Color;
import org.apache.commons.lang3.tuple.Pair;
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

    /* State of the game */
    private final Game game;

    private Color drawProponent;
    private boolean waitsForPromotion;

    private ConcurrentSkipListSet<Color> deadConnections = new ConcurrentSkipListSet<>();

    /* Synchronization tools */
    private final Semaphore semaphore;

    private final BlockingQueue<Color> surrenderQueue;
    private final BlockingQueue<Pair<Color, Message>> masterQueue;

    private final Map<Color, PlayerConnection> players;
    private final Map<Color, Reader> readerThreads;
    private final Map<Color, Writer> writerThreads;

    public Master(PlayerConnection p1, PlayerConnection p2) throws ConnectionDeadException, IOException {
        super("Master-" + p1.id() + "-" + p2.id());

        // TODO: should be done before creation of the thread, by scheduler
        /* Check if connections are alive */
        if (p1.socket().isClosed()) {
            throw new ConnectionDeadException(p1.id());
        }
        if (p2.socket().isClosed()) {
            throw new ConnectionDeadException(p2.id());
        }

        /* Initialize input channels to Master thread */
        semaphore = new Semaphore(0);
        surrenderQueue = new ArrayBlockingQueue<>(1);
        masterQueue = new LinkedBlockingQueue<>();

        /* Decide which player will be white */
        int i = RANDOM.nextInt(2); // which player is white??
        players = Map.of(
                Color.values()[  i], p1,
                Color.values()[1-i], p2
        );

        /* Initialize data and threads for players */
        writerThreads = Map.of(
                WHITE, new Writer(WHITE, players.get(WHITE).id(), players.get(WHITE).socket().getOutputStream(), this),
                BLACK, new Writer(BLACK, players.get(BLACK).id(), players.get(BLACK).socket().getOutputStream(), this)
        );
        readerThreads = Map.of(
                WHITE, new Reader(WHITE, players.get(WHITE).id(), players.get(WHITE).socket().getInputStream(),
                        this, writerThreads.get(WHITE)),
                BLACK, new Reader(BLACK, players.get(BLACK).id(), players.get(BLACK).socket().getInputStream(),
                        this, writerThreads.get(BLACK))
        );

        /* Initialize logical representation of the game */
        game = new Game(null); // TODO: implement me :)
    }

    @Override
    public void run() {
        log.info("The game between {} as {} and {} as {} is starting",
                players.get(WHITE).id(), WHITE, players.get(BLACK).id(), BLACK);

        /* start the threads */
        readerThreads.values().forEach(Thread::start);
        writerThreads.values().forEach(Thread::start);

        /* run the main loop */
        while (Thread.interrupted()) {
            try {
                // is any message available?
                semaphore.acquire();

                // check if any connection is down
                Set<Color> deadConns = deadConnections.clone();
                if (!deadConns.isEmpty()) {
                    readerThreads.values().forEach(Thread::interrupt);

                    if (deadConns.size() == 1) {
                        Color color = deadConns.iterator().next();

                        // Inform the active player that the opponent had quit
                        writerThreads.get(color.next()).pushGameOutcome(
                                new GameOutcome(GameOutcome.Type.VICTORY, "The opponent had quit"));
                    } // else there is no one to notify

                    // Kill the Writer threads operating on broken connections
                    for (Color color : deadConns) {
                        writerThreads.get(color).interrupt();
                        try {
                            players.get(color).socket().close();
                        } catch (IOException e) {
                            log.warn("Could not close socket of player {}", players.get(color).id(), e);
                        }
                    }

                    joinWorkerThreads();
                    break;
                }

                // check if player surrendered
                Color surrendered = surrenderQueue.poll();
                if (surrendered != null) {
                    Map<Color, GameOutcome> messages = new HashMap<>();
                    messages.put(surrendered, new GameOutcome(GameOutcome.Type.DEFEAT, "You've surrendered"));
                    messages.put(surrendered.next(), new GameOutcome(GameOutcome.Type.VICTORY, "The opponent surrendered"));
                    terminateGame(messages.get(WHITE), messages.get(BLACK));
                    break;
                }

                Color sender;
                Message message;
                {
                    Pair<Color, Message> messageFromPlayer = masterQueue.take();
                    sender = messageFromPlayer.getLeft();
                    message = messageFromPlayer.getRight();
                }

                // check if draw dialog takes place
                if (message instanceof DrawRequest drawMessage) {
                    if (drawMessage.action() == DrawRequest.Action.PROPOSE) {
                        if (drawProponent == null) {
                            drawProponent = sender;
                            // forward proposal
                            writerThreads.get(sender.next()).pushMessage(message);
                        } else {
                            log.warn("Cannot initiate draw negotiation by {} because {} has already started one",
                                    sender, drawProponent);
                            // TODO: reject message ??
                        }
                    } else {
                        if (drawProponent == null) {
                            log.warn("Cannot {} draw request because currently there is none", drawMessage.action());
                        } else if (drawProponent == sender) {
                            log.warn("Cannot {} draw request proposed by itself", drawMessage.action());
                        } else {
                            if (drawMessage.action() == DrawRequest.Action.ACCEPT) {
                                Map<Color, GameOutcome> messages = new HashMap<>();
                                messages.put(sender, new GameOutcome(GameOutcome.Type.DRAW, "You accepted the draw offer"));
                                messages.put(sender.next(), new GameOutcome(GameOutcome.Type.DRAW, "Your draw offer was accepted"));
                                terminateGame(messages.get(WHITE), messages.get(BLACK));
                                break;
                            } else {
                                writerThreads.get(sender.next()).pushMessage(drawMessage);
                            }
                        }
                    }

                    continue;
                }

                // check if message was move
                if (message instanceof MoveRequest) {
                    if (sender != game.getCurrentPlayer().getColor()) {
                        log.warn("Cannot push moves when because it's {} turn", sender.next());
                    } else {
                        if (message instanceof Move move) {
                            if (waitsForPromotion) {
                                log.warn("Cannot process move, because game is in state: ");
                            } else {
                                // TODO: process move
                                boolean legalMove = false;// game.isLegal(move);
                                if (!legalMove) {
                                    writerThreads.get(sender).pushMessage(new MoveRejected());
                                } else {
                                    waitsForPromotion = false;//game.doMove(move);
                                    writerThreads.get(sender).pushMessage(new MoveAccepted(waitsForPromotion));
                                }
                            }
                        } else if (message instanceof PromotionDecision promotionDecision) {
                            if (waitsForPromotion) {
                                // TODO: process promotion
                                //game.doPromotion(promotionDecision.piece());
                            } else {
                                log.warn("Game doesn't wait for promotion decision by {}", sender);
                            }
                        }
                    }

                    continue;
                }

                // cannot handle the message
                log.warn("Unknown message {}", message);
            } catch (InterruptedException e) {
                log.warn("Thread {} was interrupted", Thread.currentThread().getName());
            }
        }

        log.info("The game between {} as {} and {} as {} has ended",
                players.get(WHITE).id(), WHITE, players.get(BLACK).id(), BLACK);
    }

    private void terminateGame(GameOutcome whitesMsg, GameOutcome blacksMsg) {
        // stop listening for messages
        readerThreads.values().forEach(Thread::interrupt);

        // notify client about the end of the game
        try {
            writerThreads.get(WHITE).pushGameOutcome(whitesMsg);
        } catch (InterruptedException ex) {
            log.error("Could not publish ending message to {}", WHITE);
        }
        try {
            writerThreads.get(BLACK).pushGameOutcome(blacksMsg);
        } catch (InterruptedException ex) {
            log.error("Could not publish ending message to {}", BLACK);
        }

        // join worker threads
        joinWorkerThreads();
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
}
