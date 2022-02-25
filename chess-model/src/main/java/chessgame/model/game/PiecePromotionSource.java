package chessgame.model.game;

import chessgame.model.pieces.Piece;

public interface PiecePromotionSource {
    Piece getPromotedPiece();
}
