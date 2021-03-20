package chessgame.model.game.moves;

import chessgame.model.game.Chessboard;

/**
 * Interface representing executable moves on the chessboard.<br>
 * Possible actions are {occupy other location, capture enemy piece, promote a pawn}, and so on
 */
public interface IMove {
    void execute(Chessboard chessboard);
}
