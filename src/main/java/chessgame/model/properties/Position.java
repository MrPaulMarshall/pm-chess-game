package chessgame.model.properties;

public class Position {
    public final int x;
    public final int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Position copy() {
        return new Position(this.x, this.y);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (this == o) return true;
        return (o instanceof Position && ((Position)o).x == x && ((Position)o).y == y);
    }

}
