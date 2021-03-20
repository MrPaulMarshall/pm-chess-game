package chessgame.model.game.moves;

import chessgame.model.figures.Figure;
import chessgame.model.game.ChessBoard;
import chessgame.model.properties.Position;

public class BasicMove implements IMove {
    protected Position oldPosition;
    protected Position newPosition;
    protected Figure figure;

    public BasicMove(Figure figure, Position newPosition) {
        this.figure = figure;
        this.oldPosition = figure.getPosition();
        this.newPosition = newPosition;
    }

    @Override
    public void execute(ChessBoard chessboard) {
        chessboard.board[oldPosition.x][oldPosition.y] = null;
        chessboard.board[newPosition.x][newPosition.y] = figure;
        figure.setPosition(newPosition);
    }
}
