package com.marshall.chessgame.server.threads;

import com.marshall.chessgame.server.MatchRegister;
import com.marshall.chessgame.server.PlayerConnection;
import com.pmarshall.chessgame.api.Message;
import com.pmarshall.chessgame.api.endrequest.DrawRequest;
import com.pmarshall.chessgame.api.lobby.MatchFound;
import com.pmarshall.chessgame.api.move.request.Move;
import com.pmarshall.chessgame.api.move.request.MoveRequest;
import com.pmarshall.chessgame.api.move.request.Promotion;
import com.pmarshall.chessgame.api.move.response.MoveAccepted;
import com.pmarshall.chessgame.api.move.response.MoveRejected;
import com.pmarshall.chessgame.api.move.response.OpponentMoved;
import com.pmarshall.chessgame.api.outcome.GameOutcome;
import com.pmarshall.chessgame.model.game.Game;
import com.pmarshall.chessgame.model.game.PiecePromotionSource;
import com.pmarshall.chessgame.model.pieces.PieceType;
import com.pmarshall.chessgame.model.properties.Color;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

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
    private final UpdatablePromotionSource promotionSource = new UpdatablePromotionSource();
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
                WHITE, new Reader(WHITE, players.get(WHITE).id(), players.get(WHITE).in(), this, writerThreads.get(WHITE)),
                BLACK, new Reader(BLACK, players.get(BLACK).id(), players.get(BLACK).in(), this, writerThreads.get(BLACK))
        );

        /* Initialize logical representation of the game */
        game = new Game(promotionSource);
    }

    @Override
    public void run() {
        log.info("The game between {} as {} and {} as {} is starting",
                players.get(WHITE).id(), WHITE, players.get(BLACK).id(), BLACK);

        /* Prepare init messages */
        try {
            writerThreads.get(WHITE).pushMessage(new MatchFound(players.get(BLACK).id(), WHITE));
            writerThreads.get(WHITE).pushMessage(new MatchFound(players.get(BLACK).id(), WHITE));
        } catch (InterruptedException ignored) {
            log.warn("Thread {} interrupted before the game could start", Thread.currentThread().getName());
            return;
        }

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
                    cleanUpAtDeadConnection(deadConns);
                    joinWorkerThreads();
                    break;
                }

                // check if player surrendered
                Color surrendered = surrenderQueue.poll();
                if (surrendered != null) {
                    terminateGame(Map.of(
                            surrendered, new GameOutcome(GameOutcome.Type.DEFEAT, "You've surrendered"),
                            surrendered.next(), new GameOutcome(GameOutcome.Type.VICTORY, "The opponent surrendered")
                    ));
                    break;
                }

                Pair<Color, Message> messageFromPlayer = masterQueue.take();
                Color sender = messageFromPlayer.getLeft();
                Message message = messageFromPlayer.getRight();

                // check if draw dialog takes place
                if (message instanceof DrawRequest drawMessage) {
                    boolean gameEnded = handleDrawRequest(sender, drawMessage);
                    if (gameEnded)
                        break;
                    continue;
                }

                // check if message was move
                if (message instanceof MoveRequest move) {
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

    private void terminateGame(Map<Color, GameOutcome> messages) {
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

    private void cleanUpAtDeadConnection(Set<Color> deadConns) throws InterruptedException {
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
                players.get(color).out().close();
            } catch (IOException e) {
                log.warn("Could not close socket of player {}", players.get(color).id(), e);
            }
        }
    }

    /**
     * @return true if game has ended, false if it continues
     */
    private boolean handleDrawRequest(Color sender, DrawRequest message) throws InterruptedException {
        DrawRequest.Action action = message.action();

        if (drawProponent == null && action == DrawRequest.Action.PROPOSE) {
            drawProponent = sender;
            writerThreads.get(sender.next()).pushMessage(message);
            return false;
        }

        if (drawProponent == null) {
            log.warn("Cannot {} draw request because currently there is none", action);
            return false;
        }

        if (drawProponent == sender) {
            log.warn("Draw request already proposed by {}, cannot perform {}", sender, action);
        }

        return switch (action) {
            case ACCEPT -> {
                terminateGame(Map.of(
                        sender, new GameOutcome(GameOutcome.Type.DRAW, "You accepted the draw offer"),
                        sender.next(), new GameOutcome(GameOutcome.Type.DRAW, "Your draw offer was accepted")
                ));
                yield true;
            }
            case REJECT -> {
                writerThreads.get(sender.next()).pushMessage(message);
                yield false;
            }
            case PROPOSE -> {
                log.warn("Cannot initiate draw negotiation by {} because {} has already started one", sender, drawProponent);
                yield false;
            }
        };
    }

    /**
     * @return true if the game has ended, false if it continues
     */
    private boolean handleMoveRequest(Color sender, MoveRequest move) throws InterruptedException {
        if (sender != game.getCurrentPlayer().getColor()) {
            log.warn("Cannot push moves when because it's {} turn", sender.next());
            writerThreads.get(sender).pushMessage(new MoveRejected());
            return false;
        }

        boolean legalMove = game.isLegalMove(move.from(), move.to());
        if (!legalMove) {
            log.warn("Move {} is not legal", move);
            writerThreads.get(sender).pushMessage(new MoveRejected());
            return false;
        }

        if (game.isPromotionRequired(move.from(), move.to())) {
            if (move instanceof Promotion promotion) {
                promotionSource.decision = promotion.decision();
            } else {
                log.warn("Promotion decision not supplied with move {}", move);
                writerThreads.get(sender).pushMessage(new MoveRejected());
                return false;
            }
        }

        game.executeMove(move.from(), move.to());

        writerThreads.get(sender).pushMessage(new MoveAccepted());

        if (game.isDraw()) {
            GameOutcome drawOutcome = new GameOutcome(GameOutcome.Type.DRAW, "The game ended in a stalemate");
            terminateGame(Map.of(WHITE, drawOutcome, BLACK, drawOutcome));
            return true;
        }

        if (game.getWinner() != null) {
            Color winner = game.getWinner().getColor();
            terminateGame(Map.of(
                    winner, new GameOutcome(GameOutcome.Type.VICTORY, "Checkmate"),
                    winner.next(), new GameOutcome(GameOutcome.Type.DEFEAT, "Checkmate")
            ));
            return true;
        }

        List<MoveRequest> possibleMoves = game.getCurrentPlayer().getAllPossibleMoves().stream()
                .map(domainMove -> {
                    if (domainMove instanceof com.pmarshall.chessgame.model.moves.Promotion domainPromotion) {
                        return new Promotion(domainPromotion.getPieceToMove().getPosition(), domainPromotion.getNewPosition(), null);
                    } else {
                        return new Move(domainMove.getPieceToMove().getPosition(), domainMove.getNewPosition());
                    }
                }).collect(Collectors.toUnmodifiableList());

        writerThreads.get(sender.next()).pushMessage(new OpponentMoved(move.from(), move.to(),
                game.getCurrentPlayer().isKingChecked(), possibleMoves));

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

    private static class UpdatablePromotionSource implements PiecePromotionSource {

        private PieceType decision;

        @Override
        public PieceType getPromotedPiece() {
            PieceType type = decision;
            decision = null;
            return type;
        }
    }
}
