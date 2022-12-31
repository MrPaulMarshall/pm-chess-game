package com.pmarshall.chessgame.services;

import com.pmarshall.chessgame.model.pieces.PieceType;
import com.pmarshall.chessgame.model.properties.Color;
import javafx.scene.image.Image;

public interface ImageProvider {
    Image getImage(PieceType piece, Color color);
}
