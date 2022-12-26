package com.pmarshall.chessgame.model.game;

import com.pmarshall.chessgame.model.pieces.PieceType;

public interface PiecePromotionSource {
    PieceType getPromotedPiece();
}
