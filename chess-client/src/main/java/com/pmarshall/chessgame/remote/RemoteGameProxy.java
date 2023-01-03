package com.pmarshall.chessgame.remote;

import com.pmarshall.chessgame.api.Parser;
import com.pmarshall.chessgame.api.lobby.AssignId;
import com.pmarshall.chessgame.api.lobby.MatchFound;
import com.pmarshall.chessgame.controller.BoardScreenController;
import com.pmarshall.chessgame.model.api.LegalMove;
import com.pmarshall.chessgame.model.properties.Color;
import com.pmarshall.chessgame.model.properties.PieceType;
import com.pmarshall.chessgame.model.properties.Position;
import com.pmarshall.chessgame.model.service.Game;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;

public class RemoteGameProxy implements Game {

    private final BoardScreenController controller;

    private final Reader readerThread;
    private final Writer writerThread;

    private final Socket socket;

    // TODO: should be initialized in/before constructor constructor and thus final?
    private Color localPlayer;
    private String id;
    private String opponentId;

    private Pair<PieceType, Color>[][] board;
    private Color currentPlayer;
    private List<LegalMove> legalMoves;
    private boolean activeCheck;

    private RemoteGameProxy(BoardScreenController controller,
                            Socket socket, InputStream in, OutputStream out, String id) {
        this.controller = controller;

        this.id = id;
        this.socket = socket;

        this.writerThread = new Writer(out);
        this.readerThread = new Reader(in);
    }

    public static RemoteGameProxy connectToServer(BoardScreenController controller) throws IOException {
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
        this.legalMoves = message.legalMoves();

        // init local representation
        this.board = setUpBoard();
        this.currentPlayer = Color.WHITE;
    }

    /**
     * WHITE occupies 0th and 1st rows
     * <br/>
     * BLACK occupies 6th and 7th rows
     */
    private static Pair<PieceType, Color>[][] setUpBoard() {
        Pair<PieceType, Color>[][] board = new Pair[8][8];

        // pawns
        for (int j = 0; j < 8; j++) {
            board[1][j] = Pair.of(PieceType.PAWN, Color.WHITE);
            board[6][j] = Pair.of(PieceType.PAWN, Color.BLACK);
        }

        // rooks
        board[0][0] = Pair.of(PieceType.ROOK, Color.WHITE);
        board[0][7] = Pair.of(PieceType.ROOK, Color.WHITE);
        board[7][0] = Pair.of(PieceType.ROOK, Color.BLACK);
        board[7][7] = Pair.of(PieceType.ROOK, Color.BLACK);

        // knights
        board[0][1] = Pair.of(PieceType.KNIGHT, Color.WHITE);
        board[0][6] = Pair.of(PieceType.KNIGHT, Color.WHITE);
        board[7][1] = Pair.of(PieceType.KNIGHT, Color.BLACK);
        board[7][6] = Pair.of(PieceType.KNIGHT, Color.BLACK);

        // bishops
        board[0][2] = Pair.of(PieceType.BISHOP, Color.WHITE);
        board[0][5] = Pair.of(PieceType.BISHOP, Color.WHITE);
        board[7][2] = Pair.of(PieceType.BISHOP, Color.BLACK);
        board[7][5] = Pair.of(PieceType.BISHOP, Color.BLACK);

        // queens and kings
        board[0][3] = Pair.of(PieceType.QUEEN, Color.WHITE);
        board[0][4] = Pair.of(PieceType.KING, Color.WHITE);
        board[7][3] = Pair.of(PieceType.QUEEN, Color.BLACK);
        board[7][4] = Pair.of(PieceType.KING, Color.BLACK);

        return board;
    }

    @Override
    public Color currentPlayer() {
        return null;
    }

    @Override
    public boolean activeCheck() {
        return false;
    }

    @Override
    public boolean gameEnded() {
        return false;
    }

    @Override
    public String lastMoveInNotation() {
        return null;
    }

    @Override
    public Pair<Color, String> outcome() {
        return null;
    }

    @Override
    public Pair<PieceType, Color>[][] getBoardWithPieces() {
        Pair<PieceType, Color>[][] copy = new Pair[8][8];
        for (int i = 0; i < 8; i++) {
            System.arraycopy(board[i], 0, copy[i], 0, 8);
        }
        return copy;
    }

    @Override
    public List<LegalMove> legalMoves() {
        return legalMoves;
    }

    @Override
    public boolean executeMove(Position from, Position to) {
        return false;
    }

    @Override
    public boolean executeMove(Position from, Position to, PieceType promotion) {
        return false;
    }

    public String getId() {
        return id;
    }
}
