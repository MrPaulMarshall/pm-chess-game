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

    public String translateX() {
        char[] x_c = new char[1];
        x_c[0] = (char)(97 + this.x);
        return new String(x_c);
    }

    public String translateY() {
        return Integer.toString(this.y + 1);
    }
}
