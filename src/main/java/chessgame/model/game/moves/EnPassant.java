package chessgame.model.game.moves;

import chessgame.model.figures.Figure;
import chessgame.model.game.ChessBoard;
import chessgame.model.properties.Position;

public class EnPassant extends BasicMove {

    public EnPassant(Figure figure, Position newPosition) {
        super(figure, newPosition);
    }

    @Override
    public void execute(ChessBoard chessboard) {
        super.execute(chessboard);
        // TODO: remove captured enemy Pawn

        // in this move, enemy pawn is on the position
        //  {newPosition.x, oldPosition.y}
        Position enemyPawnPos = new Position(newPosition.x, oldPosition.y);
        // chessboard.removeFigure(enemyPawnPos);
    }
}
