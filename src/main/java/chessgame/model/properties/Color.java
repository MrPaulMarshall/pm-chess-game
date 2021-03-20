package chessgame.model.properties;

public enum Color {
    WHITE, BLACK;

    public Color next() {
        return this == WHITE ? BLACK : WHITE;
    }

    @Override
    public String toString() {
        return this == WHITE ? "White" : "Black";
    }
}
