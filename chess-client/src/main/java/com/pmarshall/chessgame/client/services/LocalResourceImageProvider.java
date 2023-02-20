package com.pmarshall.chessgame.client.services;

import com.pmarshall.chessgame.model.properties.Color;
import com.pmarshall.chessgame.model.properties.PieceType;
import javafx.scene.image.Image;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class LocalResourceImageProvider implements ImageProvider {

    private static final String DIRECTORY_PREFIX = "images/";

    private static final Map<Color, Map<PieceType, Image>> images;

    static {
        final Map<PieceType, String> pieceTypeNames = Arrays.stream(PieceType.values())
                .collect(Collectors.toUnmodifiableMap(type -> type, type -> type.name().toLowerCase()));

        images = Arrays.stream(Color.values()).collect(Collectors.toUnmodifiableMap(
                color -> color,
                color -> Arrays.stream(PieceType.values()).collect(
                        Collectors.toMap(type -> type, type -> loadImage(color, pieceTypeNames.get(type))))
        ));
    }

    @Override
    public Image getImage(PieceType piece, Color color) {
        return images.get(color).get(piece);
    }

    protected static Image loadImage(Color color, String name) {
        String path = DIRECTORY_PREFIX + color.name().toLowerCase() + "-" + name + ".png";
        return new Image(path);
    }
}
