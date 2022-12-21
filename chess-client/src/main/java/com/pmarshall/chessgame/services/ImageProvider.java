package com.pmarshall.chessgame.services;

import com.pmarshall.chessgame.model.pieces.Piece;
import javafx.scene.image.Image;

public interface ImageProvider {
    Image getImage(Piece piece);
}
