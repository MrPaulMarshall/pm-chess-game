package com.pmarshall.chessgame.client.services;

import com.pmarshall.chessgame.engine.properties.PieceType;
import com.pmarshall.chessgame.engine.properties.Color;
import javafx.scene.image.Image;

public interface ImageProvider {
    Image getImage(PieceType piece, Color color);
}
