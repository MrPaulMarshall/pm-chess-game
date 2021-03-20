package chessgame.model.properties;

public enum PlayerColor {
    WHITE, BLACK;

    public PlayerColor next() {
        return this == WHITE ? BLACK : WHITE;
    }

    @Override
    public String toString() {
        return this == WHITE ? "White" : "Black";
    }
}
