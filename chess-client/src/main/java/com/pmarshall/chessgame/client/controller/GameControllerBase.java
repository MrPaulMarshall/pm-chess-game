package com.pmarshall.chessgame.client.controller;

import com.pmarshall.chessgame.model.dto.LegalMove;
import com.pmarshall.chessgame.model.dto.Piece;
import com.pmarshall.chessgame.model.properties.Color;
import com.pmarshall.chessgame.model.properties.PieceType;
import com.pmarshall.chessgame.model.properties.Position;
import com.pmarshall.chessgame.model.service.Game;
import com.pmarshall.chessgame.client.services.ImageProvider;
import com.pmarshall.chessgame.client.services.LocalResourceImageProvider;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import com.pmarshall.chessgame.model.util.Pair;

import java.util.Collection;

public abstract class GameControllerBase {

    @FXML
    protected Label upperPlayerNameLabel;

    @FXML
    protected ImageView upperPlayerImageView;

    @FXML
    protected Label lowerPlayerNameLabel;

    @FXML
    protected ImageView lowerPlayerImageView;

    @FXML
    // Pane that contains whole board - it contains smaller panes: cells
    protected GridPane chessBoardGrid;

    @FXML
    protected TextArea chatText;

    @FXML
    protected TextField chatInputField;

    @FXML
    // Area when history of moves is displayed
    protected TextArea movesTextArea;

    protected Game game;

    protected ChessboardCell[][] chessboard;

    protected int moveCounter = 1;

    protected Position pieceChosen;

    protected volatile boolean gameEnded;

    protected Position checkedKing;

    protected final ImageProvider imageProvider = new LocalResourceImageProvider();

    protected void createBoardGrid(boolean forward) {
        chessboard = new ChessboardCell[8][8];

        for (int rank = 1; rank <= 8; rank++) {
            String rankStr = String.valueOf(forward ? 9 - rank : rank);
            Label leftLabel = new Label(rankStr);
            GridPane.setHalignment(leftLabel, HPos.CENTER);
            GridPane.setValignment(leftLabel, VPos.CENTER);
            chessBoardGrid.add(leftLabel, 0, rank);

            Label rightLabel = new Label(rankStr);
            GridPane.setHalignment(rightLabel, HPos.CENTER);
            GridPane.setValignment(rightLabel, VPos.CENTER);
            chessBoardGrid.add(rightLabel, 9, rank);
        }

        for (int rank = 0; rank < 8; rank++) {
            for (int file = 0; file < 8; file++) {
                final int constRank = rank, constFile = file;
                chessboard[rank][file] = new ChessboardCell((rank+file) % 2 == 0, e -> boardCellOnClick(constRank, constFile));

                chessBoardGrid.add(chessboard[rank][file].getPane(), file + 1, (forward ? rank : 7 - rank) + 1);
            }
        }
    }

    /**
     * Disables board, creates dialog with result, and closes the program
     * @param winner winner of the game (or null, if draw)
     * @param reason event that ended the game; should be insertable into "Ended by $reason"
     */
    public void endGame(Color winner, String reason) {
        // TODO: disable 'physical' buttons instead (i.e. disable GUI elements)
        if (gameEnded) return;

        gameEnded = true;
        refreshBoard(game.currentPlayer(), game.getBoardWithPieces());

        String result;
        if (winner == null) {
            result = "A draw by " + reason;
        } else {
            String winnerStr = winner.toString().substring(0, 1).toUpperCase() + winner.toString().substring(1);
            result = winnerStr + " has won by " + reason;
        }

        GameEndedController.initRootLayout(result);
    }

    /**
     * Reacts to player's click on the cell of the board
     */
    protected void boardCellOnClick(int rank, int file) {
        // if game has ended, board is disabled
        if (gameEnded)
            return;

        Position clickedCell = new Position(rank, file);

        // if player click on the chosen piece again, unmark it
        if (clickedCell.equals(pieceChosen)) {
            pieceChosen = null;
            repaintBackground();
            return;
        }

        // if player chosen valid move, execute it
        if (pieceChosen != null && game.isMoveLegal(pieceChosen, clickedCell)) {
            executeMove(pieceChosen, clickedCell);
            return;
        }

        Piece piece = game.getPiece(clickedCell);

        // check if user is not allowed to pick given color, e.g. in remote mode
        if (piece != null && arePlayersPiecesDisabled(piece.color())) {
            return;
        }

        // if player clicked on another of his pieces, mark it
        if (piece != null && piece.color() == game.currentPlayer()) {
            pieceChosen = clickedCell;
            choosePiece(pieceChosen, game.legalMovesFrom(pieceChosen));
        }
    }

    protected abstract boolean arePlayersPiecesDisabled(Color player);

    /**
     * Executes move in the model, calls Promotion dialog window if needed,
     * checks end-game conditions, and refreshes the board
     */
    private void executeMove(Position from, Position to) {
        boolean successfulMove;
        if (game.isPromotionRequired(from, to)) {
            successfulMove = game.executeMove(from, to, getPromotedPiece());
        } else {
            successfulMove = game.executeMove(from, to);
        }

        if (!successfulMove)
            return;

        refreshStageAfterMove(game.currentPlayer().next(), game.lastMove(), game.getBoardWithPieces(), game.outcome());
    }

    /**
     * Created dialog window with pieces that player can choose during promotion
     * @return piece chosen by player
     */
    private PieceType getPromotedPiece() {
        return PromotionController.askForPromotionPiece(game.currentPlayer());
    }

    public void refreshStageAfterMove(Color player, LegalMove move, Piece[][] board, Pair<Color, String> gameOutcome) {
        pieceChosen = null;
        checkedKing = move.check() ? findCheckedKing(player.next(), board) : null;

        appendMoveToLedger(player, move.notation());
        refreshBoard(player.next(), board);

        // check win conditions
        if (gameOutcome != null) {
            endGame(gameOutcome.left(), gameOutcome.right());
        }
    }

    /**
     * Reloads whole information about state of the game
     */
    protected void refreshBoard(Color player, Piece[][] board) {
        repaintBackground();
        for (int rank = 0; rank < 8; rank++) {
            for (int file = 0; file < 8; file++) {
                Piece piece = board[rank][file];
                Image image = piece == null ? null : imageProvider.getImage(piece.piece(), piece.color());
                chessboard[rank][file].setImage(image);
            }
        }
    }

    /**
     * Sets all cells into their idle state, but shows the active check
     */
    private void repaintBackground() {
        for (int rank = 0; rank < 8; rank++) {
            for (int file = 0; file < 8; file++) {
                chessboard[rank][file].refreshBackground();
            }
        }

        if (checkedKing != null) {
            markCheckedKingsField();
        }
    }

    /**
     * Refreshes the board coloring, then marks chosen piece and possible destinations
     */
    private void choosePiece(Position picked, Collection<Position> legalTargets) {
        repaintBackground();
        chessboard[picked.rank()][picked.file()].setChosenBackground();
        for (Position target : legalTargets) {
            chessboard[target.rank()][target.file()].setClickableBackground();
        }
    }

    /**
     * Prints last move into text area
     */
    private void appendMoveToLedger(Color player, String notation) {
        movesTextArea.setEditable(true);
        if (player == Color.WHITE) {
            movesTextArea.appendText(moveCounter + ". " + notation);
        } else {
            movesTextArea.appendText(" " + notation + "\n");
            moveCounter++;
        }
        movesTextArea.setEditable(false);
    }

    private Position findCheckedKing(Color color, Piece[][] board) {
        for (int rank = 0; rank < 8; rank++) {
            for (int file = 0; file < 8; file++) {
                Piece piece = board[rank][file];
                if (piece != null && piece.piece() == PieceType.KING && piece.color() == color) {
                    return new Position(rank, file);
                }
            }
        }
        throw new IllegalStateException("King must be on the board");
    }

    private void markCheckedKingsField() {
        chessboard[checkedKing.rank()][checkedKing.file()].setCheckedBackground();
    }

}
