package com.pmarshall.chessgame.services;

import com.pmarshall.chessgame.model.pieces.*;
import com.pmarshall.chessgame.model.properties.Color;
import javafx.scene.image.Image;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.pmarshall.chessgame.model.properties.Color.BLACK;
import static com.pmarshall.chessgame.model.properties.Color.WHITE;

public class LocalResourceImageProvider implements ImageProvider {

    private static final String DIRECTORY_PREFIX = "images/";

    private static final Map<Color, Map<Class<? extends Piece>, Image>> images;

    static {
        final List<Class<? extends Piece>> pieceTypes = List.of(
                Bishop.class, King.class, Knight.class, Pawn.class, Queen.class, Rook.class);

        final Map<Class<? extends Piece>, String> pieceTypeNames = Map.of(
                Bishop.class, "bishop",
                King.class, "king",
                Knight.class, "knight",
                Pawn.class, "pawn",
                Queen.class, "queen",
                Rook.class, "rook");

        Map<Class<? extends Piece>, Image> whitePieces = pieceTypes.stream()
                .collect(Collectors.toMap(type -> type, type -> loadImage(WHITE, pieceTypeNames.get(type))));

        Map<Class<? extends Piece>, Image> blackPieces = pieceTypes.stream()
                .collect(Collectors.toMap(type -> type, type -> loadImage(BLACK, pieceTypeNames.get(type))));

        images = Map.of(
                WHITE, whitePieces,
                BLACK, blackPieces
        );
    }

    @Override
    public Image getImage(Piece piece) {
        return images.get(piece.getColor()).get(piece.getClass());
    }

    protected static Image loadImage(Color color, String name) {
        String path = DIRECTORY_PREFIX + (color == WHITE ? "white" : "black") + "-" + name + ".png";
        return new Image(path, 50, 50, false,true, false);
    }
}
