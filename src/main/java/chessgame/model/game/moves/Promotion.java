package chessgame.model.game.moves;

import chessgame.model.figures.Figure;
import chessgame.model.game.Game;

public class Promotion extends Move {

    private BasicMove basicMove;
    private Figure newPiece;

    public Promotion(BasicMove basicMove) {
        this.basicMove = basicMove;

        this.movedFigure = basicMove.movedFigure;
        this.oldPosition = basicMove.oldPosition;
        this.newPosition = basicMove.newPosition;
        this.takenPiece = basicMove.takenPiece;
        this.takenPiecePosition = basicMove.takenPiecePosition;

        this.newPiece = null;
    }

    @Override
    public void execute(Game game) {
        // execute basic move
        this.basicMove.execute(game);
        game.getCurrentPlayer().getPieces().remove(basicMove.movedFigure);

        // change pawn for another piece
        if (game.isRealChessboard()) {
            this.newPiece = game.askForPromotedPiece();
            game.board[this.basicMove.newPosition.x][this.basicMove.newPosition.y] = this.newPiece;
            game.getCurrentPlayer().getPieces().add(this.newPiece);
        }
    }

    @Override
    public void undo(Game game) {
        if (game.isRealChessboard()) {
            game.getCurrentPlayer().getPieces().remove(this.newPiece);
            game.board[this.basicMove.newPosition.x][this.basicMove.newPosition.y] = null;
        }

        game.getCurrentPlayer().getPieces().add(basicMove.movedFigure);
        this.basicMove.undo(game);
    }

    @Override
    public boolean equals(Object other) {
        return false;
    }
}
