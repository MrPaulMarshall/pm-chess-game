package chessgame.model.game;

import chessgame.model.figures.Figure;
import chessgame.model.properties.Position;

/**
 * Class that represent the game;
 * It contains chessboard itself, players, and pieces.<br>
 *
 * In order to check whether move is valid (doesn't endanger a king)
 * this object can be copied, and simulation can be performed on that copy
 */
public class ChessBoard {
    public final boolean realChessboard;

    public final Figure[][] board = new Figure[8][8];
    public Player currentPlayer;

    public Player whitePlayer;
    public Player blackPlayer;

    public ChessBoard(Player white, Player black, boolean realChessboard) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                this.board[i][j] = null;
            }
        }

        this.whitePlayer = white;
        this.blackPlayer = black;

        this.currentPlayer = white;

        this.realChessboard = realChessboard;
    }

    public void changePlayer() {
        this.currentPlayer = this.currentPlayer == this.whitePlayer ? this.blackPlayer : this.whitePlayer;
    }

    public void movePiece(Figure figure, Position newPosition) {
//        Position oldP = figure.getPosition();
//        this.board[oldP.getX()][oldP.getY()] = null;
//        this.board[newPosition.getX()][newPosition.getY()] = figure;
//        figure.setPosition(newPosition);
    }
}
