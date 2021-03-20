package chessgame.model.game;

import chessgame.model.figures.Figure;
import chessgame.model.properties.PlayerColor;

import java.util.LinkedList;
import java.util.List;

public class Player {
    private final PlayerColor playerColor;

    private List<Figure> pieces = new LinkedList<>();

    public Player(PlayerColor playerColor) {
        this.playerColor = playerColor;
    }
}
