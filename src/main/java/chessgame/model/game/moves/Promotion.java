package chessgame.model.game.moves;

import chessgame.model.pieces.Piece;
import chessgame.model.game.Game;

public class Promotion extends Move {

    private BasicMove basicMove;
    private Piece newPiece;

    public Promotion(BasicMove basicMove) {
        this.basicMove = basicMove;

        this.movedPiece = basicMove.movedPiece;
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

        // change pawn for another piece
        if (game.isRealChessboard()) {
            game.getCurrentPlayer().getPieces().remove(basicMove.movedPiece);

            this.newPiece = game.askForPromotedPiece();
            this.newPiece.setPosition(this.basicMove.newPosition);
            game.board[this.basicMove.newPosition.x][this.basicMove.newPosition.y] = this.newPiece;
            game.getCurrentPlayer().getPieces().add(this.newPiece);
        }
    }

    @Override
    public void undo(Game game) {
        if (game.isRealChessboard()) {
            game.getCurrentPlayer().getPieces().remove(this.newPiece);
            game.board[this.basicMove.newPosition.x][this.basicMove.newPosition.y] = null;

            game.getCurrentPlayer().getPieces().add(basicMove.movedPiece);
        }

        this.basicMove.undo(game);
    }

    @Override
    public boolean equals(Object other) {
        return false;
    }
}
