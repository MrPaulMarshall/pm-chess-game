package chessgame.model.moves;

import chessgame.model.game.Game;
import chessgame.model.pieces.Piece;

/**
 * @author Paweł Marszał
 *
 * Represents promotion of pawn into big piece: Queen, Knight, Rook or Bishop
 */
public class Promotion extends Move {

    /**
     * Basic move that leads to pawn being in the last row
     */
    private final BasicMove basicMove;

    /**
     * New piece, that player has chosen
     */
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

        // exchange pawn for new piece
        if (game.getGameMode()) {
            game.getCurrentPlayer().getPieces().remove(basicMove.movedPiece);

            this.newPiece = game.askForPromotedPiece();
            this.newPiece.setPosition(this.basicMove.newPosition);
            game.board[this.basicMove.newPosition.x][this.basicMove.newPosition.y] = this.newPiece;
            game.getCurrentPlayer().getPieces().add(this.newPiece);
        }
    }

    @Override
    public void undo(Game game) {
        // undo exchanging pawn
        if (game.getGameMode()) {
            game.getCurrentPlayer().getPieces().remove(this.newPiece);
            game.board[this.basicMove.newPosition.x][this.basicMove.newPosition.y] = null;

            game.getCurrentPlayer().getPieces().add(basicMove.movedPiece);
        }

        // undo move that lead to pawn being on the last row
        this.basicMove.undo(game);
    }

    @Override
    public String toString() {
        return (this.takenPiece == null ? "" : this.oldPosition.translateX() + "x")
                + this.newPosition.translateX() + this.newPosition.translateY() + this.newPiece.toString();
    }

}
