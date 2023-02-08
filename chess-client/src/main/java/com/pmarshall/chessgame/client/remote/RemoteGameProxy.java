package com.pmarshall.chessgame.client.remote;

import com.pmarshall.chessgame.api.ChatMessage;
import com.pmarshall.chessgame.api.Message;
import com.pmarshall.chessgame.api.Parser;
import com.pmarshall.chessgame.api.endrequest.DrawProposition;
import com.pmarshall.chessgame.api.endrequest.DrawResponse;
import com.pmarshall.chessgame.api.endrequest.Surrender;
import com.pmarshall.chessgame.api.lobby.MatchFound;
import com.pmarshall.chessgame.api.move.Move;
import com.pmarshall.chessgame.api.move.OpponentMoved;
import com.pmarshall.chessgame.api.outcome.GameOutcome;
import com.pmarshall.chessgame.client.controller.RemoteGameController;
import com.pmarshall.chessgame.engine.dto.*;
import com.pmarshall.chessgame.engine.properties.Color;
import com.pmarshall.chessgame.engine.properties.PieceType;
import com.pmarshall.chessgame.engine.properties.Position;
import com.pmarshall.chessgame.engine.service.Game;
import javafx.application.Platform;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RemoteGameProxy implements Game, ServerProxy {

    private static final Logger log = LoggerFactory.getLogger(RemoteGameProxy.class);

    private final RemoteGameController controller;

    private final Reader readerThread;
    private final Writer writerThread;

    private final BlockingQueue<Message> messagesToServer;

    private final Socket socket;

    private final Color localPlayer;
    private final String id;
    private final String opponentId;

    private final Piece[][] board;
    private Color currentPlayer;

    private Map<Pair<Position, Position>, LegalMove> legalMoves;
    private Map<Triple<Position, Position, PieceType>, Promotion> legalPromotions;

    private LegalMove lastMove;
    private GameOutcome outcome;

    public RemoteGameProxy(RemoteGameController controller, ServerConnection connection, String id, MatchFound message) {
        this.controller = controller;

        this.id = id;
        this.socket = connection.socket();

        // init local representation
        this.localPlayer = message.color();
        this.opponentId = message.opponentId();

        this.board = setUpBoard();
        this.currentPlayer = Color.WHITE;
        storeLegalMoves(message.legalMoves());

        // threads and message queue
        this.messagesToServer = new LinkedBlockingQueue<>();

        this.writerThread = new Writer(connection.out());
        this.readerThread = new Reader(connection.in());

        // start worker threads
        writerThread.start();
        readerThread.start();
    }

    private void storeLegalMoves(List<LegalMove> moves) {
        legalMoves = moves.stream()
                .filter(move -> !(move instanceof Promotion))
                .collect(Collectors.toUnmodifiableMap(m -> Pair.of(m.from(), m.to()), move -> move));
        legalPromotions = moves.stream()
                .filter(move -> move instanceof Promotion)
                .map(Promotion.class::cast)
                .collect(Collectors.toUnmodifiableMap(m -> Triple.of(m.from(), m.to(), m.newType()), move -> move));
    }

    /**
     * WHITE occupies 0th and 1st rows
     * <br/>
     * BLACK occupies 6th and 7th rows
     */
    private static Piece[][] setUpBoard() {
        Piece[][] board = new Piece[8][8];

        // pawns
        for (int file = 0; file < 8; file++) {
            board[6][file] = new Piece(PieceType.PAWN, Color.WHITE);
            board[1][file] = new Piece(PieceType.PAWN, Color.BLACK);
        }

        // rooks
        board[7][0] = new Piece(PieceType.ROOK, Color.WHITE);
        board[7][7] = new Piece(PieceType.ROOK, Color.WHITE);
        board[0][0] = new Piece(PieceType.ROOK, Color.BLACK);
        board[0][7] = new Piece(PieceType.ROOK, Color.BLACK);

        // knights
        board[7][1] = new Piece(PieceType.KNIGHT, Color.WHITE);
        board[7][6] = new Piece(PieceType.KNIGHT, Color.WHITE);
        board[0][1] = new Piece(PieceType.KNIGHT, Color.BLACK);
        board[0][6] = new Piece(PieceType.KNIGHT, Color.BLACK);

        // bishops
        board[7][2] = new Piece(PieceType.BISHOP, Color.WHITE);
        board[7][5] = new Piece(PieceType.BISHOP, Color.WHITE);
        board[0][2] = new Piece(PieceType.BISHOP, Color.BLACK);
        board[0][5] = new Piece(PieceType.BISHOP, Color.BLACK);

        // queens and kings
        board[7][3] = new Piece(PieceType.QUEEN, Color.WHITE);
        board[7][4] = new Piece(PieceType.KING, Color.WHITE);
        board[0][3] = new Piece(PieceType.QUEEN, Color.BLACK);
        board[0][4] = new Piece(PieceType.KING, Color.BLACK);

        return board;
    }

    @Override
    public Color localPlayer() {
        return localPlayer;
    }

    @Override
    public void surrender() throws InterruptedException {
        messagesToServer.put(new Surrender());
    }

    @Override
    public void proposeDraw() throws InterruptedException {
        messagesToServer.put(new DrawProposition());
    }

    @Override
    public Color currentPlayer() {
        return currentPlayer;
    }

    @Override
    public LegalMove lastMove() {
        return lastMove;
    }

    @Override
    public Pair<Color, String> outcome() {
        if (outcome == null)
            return null;

        return Pair.of(switch (outcome.outcome()) {
            case VICTORY -> localPlayer;
            case DEFEAT -> localPlayer.next();
            case DRAW -> null;
        }, outcome.message());
    }

    @Override
    public Piece[][] getBoardWithPieces() {
        Piece[][] copy = new Piece[8][8];
        for (int i = 0; i < 8; i++) {
            System.arraycopy(board[i], 0, copy[i], 0, 8);
        }
        return copy;
    }

    @Override
    public Piece getPiece(Position on) {
        return board[on.rank()][on.file()];
    }

    @Override
    public boolean isMoveLegal(Position from, Position to) {
        boolean legalPromotion = legalPromotions.keySet().stream()
                .anyMatch(triple -> triple.getLeft().equals(from) && triple.getMiddle().equals(to));

        return legalMoves.containsKey(Pair.of(from, to)) || legalPromotion;
    }

    @Override
    public Collection<Position> legalMovesFrom(Position from) {
        Stream<Position> destinations = legalMoves.values().stream()
                .filter(move -> move.from().equals(from)).map(LegalMove::to).distinct();

        Stream<Position> promotionsDestinations = legalPromotions.values().stream()
                .filter(move -> move.from().equals(from)).map(Promotion::to).distinct();

        return Stream.concat(destinations, promotionsDestinations).collect(Collectors.toList());
    }

    @Override
    public boolean isPromotionRequired(Position from, Position to) {
        return legalPromotions.keySet().stream().anyMatch(
                triple -> triple.getLeft().equals(from) && triple.getMiddle().equals(to));
    }

    @Override
    public boolean executeMove(Position from, Position to) {
        if (!legalMoves.containsKey(Pair.of(from, to))) {
            return false;
        }

        LegalMove move = legalMoves.get(Pair.of(from, to));
        executeMove(move);

        try {
            messagesToServer.put(new Move(from, to, null));
        } catch (InterruptedException ex) {
            // TODO: escalate exception to main Game-loop?
            return false;
        }

        return true;
    }

    @Override
    public boolean executeMove(Position from, Position to, PieceType promotion) {
        if (!legalPromotions.containsKey(Triple.of(from, to, promotion))) {
            return false;
        }

        Promotion move = legalPromotions.get(Triple.of(from, to, promotion));
        executeMove(move);

        try {
            messagesToServer.put(new Move(from, to, promotion));
        } catch (InterruptedException ex) {
            // TODO: escalate exception to main Game-loop?
            return false;
        }

        return true;
    }

    /**
     * Updates the board and other data about the state of the game.
     * <p>
     * Sets the other player as the current one.
     */
    private void executeMove(LegalMove move) {
        board[move.to().rank()][move.to().file()] = board[move.from().rank()][move.from().file()];
        board[move.from().rank()][move.from().file()] = null;

        if (move instanceof Promotion p) {
            board[move.to().rank()][move.to().file()] = new Piece(p.newType(), currentPlayer);
        }
        if (move instanceof Castling c) {
            if (c.queenSide()) {
                board[move.from().rank()][3] = board[move.from().rank()][0];
                board[move.from().rank()][0] = null;
            } else {
                board[move.from().rank()][5] = board[move.from().rank()][7];
                board[move.from().rank()][7] = null;
            }
        }
        if (move instanceof EnPassant e) {
            board[e.takenPawn().rank()][e.takenPawn().file()] = null;
        }

        lastMove = move;

        legalMoves = Map.of();
        legalPromotions = Map.of();

        currentPlayer = currentPlayer.next();
    }


    public String getId() {
        return id;
    }

    public void terminateGame(Color winner) {
        writerThread.interrupt();
        readerThread.interrupt();
        try {
            socket.close();
        } catch (IOException ex) {
            log.warn("Could not close connection to server", ex);
        }

        Platform.runLater(() -> controller.endGame(winner));
    }

    @Override
    public void acceptDraw() throws InterruptedException {
        messagesToServer.put(new DrawResponse(true));
    }

    @Override
    public void rejectDraw() throws InterruptedException {
        messagesToServer.put(new DrawResponse(false));
    }

    @Override
    public void pushChatMessage(String message) throws InterruptedException {
        messagesToServer.put(new ChatMessage(message));
    }

    public class Reader extends Thread {

        private static final Logger log = LoggerFactory.getLogger(Reader.class);

        private final InputStream in;

        public Reader(InputStream in) {
            this.in = in;
        }

        @Override
        public void run() {
            while (!Thread.interrupted()) {
                try {
                    byte[] lengthHeader = in.readNBytes(2);
                    int length = Parser.deserializeLength(lengthHeader);
                    byte[] messageBytes = in.readNBytes(length);
                    Message msg = Parser.deserialize(messageBytes, length);

                    if (msg instanceof GameOutcome outcomeMsg) {
                        outcome = outcomeMsg;
                        Color winner = switch (outcomeMsg.outcome()) {
                            case VICTORY -> localPlayer;
                            case DEFEAT -> localPlayer.next();
                            case DRAW -> null;
                        };
                        terminateGame(winner);
                        break;
                    }
                    if (msg instanceof OpponentMoved opponentMoved) {
                        executeMove(opponentMoved.move());
                        storeLegalMoves(opponentMoved.legalMoves());
                        Platform.runLater(() ->
                                controller.refreshStageAfterMove(currentPlayer.next(), lastMove, board, outcome()));
                        continue;
                    }
                    if (msg instanceof ChatMessage chatMsg) {
                        log.info("CHAT: {} says {}", opponentId, chatMsg.text());
                        Platform.runLater(() ->
                                controller.appendToChat(localPlayer.next(), chatMsg.text()));
                        continue;
                    }
                    if (msg instanceof DrawProposition) {
                        Platform.runLater(controller::showDrawRequestedWindow);
                        continue;
                    }

                    log.warn("Unrecognized message: {}", msg);
    //            } catch (InterruptedException ex) { // TODO: uncomment when the blocking operation will be added

                } catch (IOException ex) {
                    log.error("Connection with server was broken in Reader", ex);
                    terminateGame(null);
                }
            }

            log.info("Reader finishes");
        }

    }

    public class Writer extends Thread {

        private static final Logger log = LoggerFactory.getLogger(Writer.class);

        private final OutputStream out;

        public Writer(OutputStream out) {
            this.out = out;
        }

        @Override
        public void run() {
            while (!Thread.interrupted()) {
                try {
                    Message msg = messagesToServer.take();

                    byte[] messageBytes = Parser.serialize(msg);
                    byte[] lengthHeader = Parser.serializeLength(messageBytes.length);

                    out.write(lengthHeader);
                    out.write(messageBytes);
                } catch (InterruptedException ex) {
                    interrupt();
                    log.warn("Thread Writer was interrupted");
                } catch (IOException ex) {
                    log.warn("Connection with server was broken in Writer", ex);
                    terminateGame(null);
                }
            }

            log.info("Writer finishes");
        }
    }
}
