package com.pmarshall.chessgame.model.properties;

/**
 * @author Paweł Marszał
 * <p>
 * Class representing position on the board
 * x - column
 * y - row
 * <p>
 * They map from in-game coordinates into chess-like coordinates as follows:
 * x: {0, 1, .., 7} -> {'a', 'b', .., 'h'}
 * y: {0, 1, .., 7} -> {'8', '7', .., '1'}
 */
public record Position(int x, int y) {

    /**
     * Translates numerical in-game indices into literal "chess-like" indices
     *
     * @return 'a', 'b', .., 'h' for successive columns (left-to-right)
     */
    public String translateX() {
        char[] x_c = new char[1];
        x_c[0] = (char) (97 + this.x);
        return new String(x_c);
    }

    /**
     * Translates numerical in-game indices into literal "chess-like" indices
     *
     * @return '1', '2', .., '8' for successive rows (bottom-up)
     */
    public String translateY() {
        return Integer.toString(8 - this.y);
    }
}
