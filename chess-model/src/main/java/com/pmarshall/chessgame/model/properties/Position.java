package com.pmarshall.chessgame.model.properties;

/**
 * @author Paweł Marszał
 *
 * Class representing position on the board
 *  x - column
 *  y - row
 *
 * They map from in-game coordinates into chess-like coordinates as follows:
 *  x: {0, 1, .., 7} -> {'a', 'b', .., 'h'}
 *  y: {0, 1, .., 7} -> {'8', '7', .., '1'}
 */
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

    /**
     * Translates numerical in-game indices into literal "chess-like" indices
     * @return 'a', 'b', .., 'h' for successive columns (left-to-right)
     */
    public String translateX() {
        char[] x_c = new char[1];
        x_c[0] = (char)(97 + this.x);
        return new String(x_c);
    }

    /**
     * Translates numerical in-game indices into literal "chess-like" indices
     * @return '1', '2', .., '8' for successive rows (bottom-up)
     */
    public String translateY() {
        return Integer.toString(8 - this.y);
    }
}
