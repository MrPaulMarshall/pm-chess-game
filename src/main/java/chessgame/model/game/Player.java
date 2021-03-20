package chessgame.model.game;

import chessgame.model.figures.Figure;
import chessgame.model.properties.Color;

import java.util.LinkedList;
import java.util.List;

public class Player {
    private final Color color;

    private List<Figure> pieces = new LinkedList<>();

    public Player(Color color) {
        this.color = color;
    }
}
