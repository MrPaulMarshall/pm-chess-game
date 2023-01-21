package com.pmarshall.chessgame.remote;

import com.pmarshall.chessgame.api.ChatMessage;
import com.pmarshall.chessgame.api.Message;
import com.pmarshall.chessgame.api.Parser;
import com.pmarshall.chessgame.api.endrequest.DrawProposition;
import com.pmarshall.chessgame.api.endrequest.DrawResponse;
import com.pmarshall.chessgame.api.endrequest.Surrender;
import com.pmarshall.chessgame.api.lobby.AssignId;
import com.pmarshall.chessgame.api.lobby.MatchFound;
import com.pmarshall.chessgame.api.move.Move;
import com.pmarshall.chessgame.api.move.OpponentMoved;
import com.pmarshall.chessgame.api.outcome.GameOutcome;
import com.pmarshall.chessgame.controller.RemoteGameController;
import com.pmarshall.chessgame.model.dto.LegalMove;
import com.pmarshall.chessgame.model.dto.Piece;
import com.pmarshall.chessgame.model.dto.Promotion;
import com.pmarshall.chessgame.model.properties.Color;
import com.pmarshall.chessgame.model.properties.PieceType;
import com.pmarshall.chessgame.model.properties.Position;
import com.pmarshall.chessgame.model.service.Game;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

public class RemoteGameProxy implements Game, ServerProxy {

    private static final Logger log = LoggerFactory.getLogger(RemoteGameProxy.class);

    private final RemoteGameController controller;

    private final Reader readerThread;
    private final Writer writerThread;

    private final BlockingQueue<Message> messagesToServer;

    private final Socket socket;

    // TODO: should be initialized in/before constructor constructor and thus final?
    private Color localPlayer;
    private String id;
    private String opponentId;

    private Piece[][] board;
    private Color currentPlayer;

    private Map<Pair<Position, Position>, LegalMove> legalMoves;
    private String lastMoveInNotation;
    private boolean activeCheck;
    private GameOutcome outcome;

    private RemoteGameProxy(RemoteGameController controller,
                            Socket socket, InputStream in, OutputStream out, String id) {
        this.controller = controller;

        this.id = id;
        this.socket = socket;

        this.messagesToServer = new LinkedBlockingQueue<>();
        this.writerThread = new Writer(out);
        this.readerThread = new Reader(in);
    }

    public static RemoteGameProxy connectToServer(RemoteGameController controller) throws IOException {
        Socket socket = new Socket("127.0.0.1", 21370);
        InputStream in = socket.getInputStream();
        OutputStream out = socket.getOutputStream();

        String id = waitForIdAssignment(in);

        return new RemoteGameProxy(controller, socket, in, out, id);
    }

    private static String waitForIdAssignment(InputStream in) throws IOException {
        byte[] headerBuffer = in.readNBytes(2);
        int length = Parser.deserializeLength(headerBuffer);
        byte[] messageBuffer = in.readNBytes(length);

        // if the message is different that AssignId, then the contract is broken and client cannot continue
        AssignId message = (AssignId) Parser.deserialize(messageBuffer, length);
        return message.id();
    }

    public void waitForOpponentMatch(InputStream in) throws IOException {
        byte[] headerBuffer = in.readNBytes(2);
        int length = Parser.deserializeLength(headerBuffer);
        byte[] messageBuffer = in.readNBytes(length);

        // if the message is different that MatchFound, then the contract is broken and client cannot continue
        MatchFound message = (MatchFound) Parser.deserialize(messageBuffer, length);
        this.localPlayer = message.color();
        this.opponentId = message.opponentId();
        this.legalMoves = message.legalMoves().stream().collect(
                Collectors.toUnmodifiableMap(move -> Pair.of(move.from(), move.to()), move -> move));

        // init local representation
        this.board = setUpBoard();
        this.currentPlayer = Color.WHITE;
    }

    /**
     * WHITE occupies 0th and 1st rows
     * <br/>
     * BLACK occupies 6th and 7th rows
     */
    private static Piece[][] setUpBoard() {
        Piece[][] board = new Piece[8][8];

        // pawns
        for (int j = 0; j < 8; j++) {
            board[1][j] = new Piece(PieceType.PAWN, Color.WHITE);
            board[6][j] = new Piece(PieceType.PAWN, Color.BLACK);
        }

        // rooks
        board[0][0] = new Piece(PieceType.ROOK, Color.WHITE);
        board[0][7] = new Piece(PieceType.ROOK, Color.WHITE);
        board[7][0] = new Piece(PieceType.ROOK, Color.BLACK);
        board[7][7] = new Piece(PieceType.ROOK, Color.BLACK);

        // knights
        board[0][1] = new Piece(PieceType.KNIGHT, Color.WHITE);
        board[0][6] = new Piece(PieceType.KNIGHT, Color.WHITE);
        board[7][1] = new Piece(PieceType.KNIGHT, Color.BLACK);
        board[7][6] = new Piece(PieceType.KNIGHT, Color.BLACK);

        // bishops
        board[0][2] = new Piece(PieceType.BISHOP, Color.WHITE);
        board[0][5] = new Piece(PieceType.BISHOP, Color.WHITE);
        board[7][2] = new Piece(PieceType.BISHOP, Color.BLACK);
        board[7][5] = new Piece(PieceType.BISHOP, Color.BLACK);

        // queens and kings
        board[0][3] = new Piece(PieceType.QUEEN, Color.WHITE);
        board[0][4] = new Piece(PieceType.KING, Color.WHITE);
        board[7][3] = new Piece(PieceType.QUEEN, Color.BLACK);
        board[7][4] = new Piece(PieceType.KING, Color.BLACK);

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
    public boolean activeCheck() {
        return activeCheck;
    }

    @Override
    public boolean gameEnded() {
        return outcome != null;
    }

    @Override
    public String lastMoveInNotation() {
        return lastMoveInNotation;
    }

    @Override
    public Pair<Color, String> outcome() {
        return Pair.of(null, outcome.message());
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
        return board[on.x()][on.y()];
    }

    @Override
    public boolean isMoveLegal(Position from, Position to) {
        return legalMoves.containsKey(Pair.of(from, to));
    }

    @Override
    public Collection<Position> legalMovesFrom(Position from) {
        return legalMoves.values().stream()
                .filter(move -> move.from().equals(from))
                .map(LegalMove::to).collect(Collectors.toList());
    }

    @Override
    public boolean isPromotionRequired(Position from, Position to) {
        return legalMoves.get(Pair.of(from, to)) instanceof Promotion;
    }

    @Override
    public boolean executeMove(Position from, Position to) {
        if (!legalMoves.containsKey(Pair.of(from, to))) {
            return false;
        }

        LegalMove move = legalMoves.get(Pair.of(from, to));

        board[to.x()][to.y()] = board[from.x()][from.y()];
        board[from.x()][from.y()] = null;

        currentPlayer = localPlayer.next();
//        activeCheck = move.check();
        legalMoves = Map.of();
//        lastMoveInNotation = move.notation();

        controller.refreshBoard();

        try {
            messagesToServer.put(new Move(from, to, null));
        } catch (InterruptedException ex) {
            // TODO: escalate exception to main Game-loop?
            return false;
        }

        // TODO: update GUI

        // wait for response
        return true;
    }

    @Override
    public boolean executeMove(Position from, Position to, PieceType promotion) {
        if (!legalMoves.containsKey(Pair.of(from, to))) {
            return false;
        }

        LegalMove move = legalMoves.get(Pair.of(from, to));

        board[to.x()][to.y()] = new Piece(promotion, board[from.x()][from.y()].color());
        board[from.x()][from.y()] = null;

        currentPlayer = localPlayer.next();
//        activeCheck = move.withCheck();
        legalMoves = Map.of();
//        lastMoveInNotation = move.notation();

        controller.refreshBoard();

        try {
            messagesToServer.put(new Move(from, to, promotion));
        } catch (InterruptedException ex) {
            // TODO: escalate exception to main Game-loop?
            return false;
        }

        // TODO: update GUI

        return true;
    }

    public String getId() {
        return id;
    }

//    @Override
    public void terminateGame() {
        writerThread.interrupt();
        readerThread.interrupt();
        try {
            socket.close();
        } catch (IOException ex) {
            log.warn("Could not close connection to server", ex);
        }

        // TODO: controller.endGame();
    }

    @Override
    public void acceptDraw() throws InterruptedException {
        messagesToServer.put(new DrawResponse(true));
    }

    @Override
    public void rejectDraw() throws InterruptedException {
        messagesToServer.put(new DrawResponse(false));
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
                        terminateGame();
                        break;
                    }
                    if (msg instanceof OpponentMoved move) {
                        if (move.promotion() != null) {
                            board[move.to().x()][move.to().y()] = new Piece(move.promotion(), localPlayer.next());
                        } else {
                            board[move.to().x()][move.to().y()] = board[move.from().x()][move.from().y()];
                        }
                        board[move.from().x()][move.from().y()] = null;

                        currentPlayer = localPlayer;
                        activeCheck = move.withCheck();
                        legalMoves = move.legalMoves().stream().collect(
                                Collectors.toUnmodifiableMap(m -> Pair.of(m.from(), m.to()), m -> m));
                        lastMoveInNotation = move.moveRepresentation();

                        controller.refreshBoard();

                        continue;
                    }
                    if (msg instanceof ChatMessage chatMsg) {
                        // TODO: add chat window
                        log.info("CHAT: {} says {}", opponentId, chatMsg.text());
                        continue;
                    }
                    if (msg instanceof DrawProposition drawProposition) {
                        // TODO: if exists, close the window opened by this player
                        // TODO: void controller::showDrawRequestWindow();
                        continue;
                    }

                    log.warn("Unrecognized message: {}", msg);
    //            } catch (InterruptedException ex) { // TODO: uncomment when the blocking operation will be added

                } catch (IOException ex) {
                    log.error("Connection with server was broken in Reader", ex);
                    terminateGame();
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
                    log.warn("Thread Writer was interrupted");
                } catch (IOException ex) {
                    log.warn("Connection with server was broken in Writer", ex);
                    terminateGame();
                }
            }

            log.info("Writer finishes");
        }
    }
}
