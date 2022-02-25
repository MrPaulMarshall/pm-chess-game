package chessgame.services;

import chessgame.model.pieces.Piece;
import javafx.scene.image.Image;

public interface ImageProvider {
    Image getImage(Piece piece);
}
