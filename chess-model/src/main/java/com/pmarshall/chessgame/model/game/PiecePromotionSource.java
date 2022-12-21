package com.pmarshall.chessgame.model.game;

import com.pmarshall.chessgame.model.pieces.Piece;

public interface PiecePromotionSource {
    Piece getPromotedPiece();
}
