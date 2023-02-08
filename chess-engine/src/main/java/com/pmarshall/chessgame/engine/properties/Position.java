package com.pmarshall.chessgame.engine.properties;

/**
 * @author Paweł Marszał
 * <p>
 * Class representing position on the board
 * rank - row
 * file - column
 * <p>
 * They map from in-game coordinates into chess-like coordinates as follows:
 * rank: {0, 1, .., 7} -> {'8', '7', .., '1'}
 * file: {0, 1, .., 7} -> {'a', 'b', .., 'h'}
 */
public record Position(int rank, int file) {

    /**
     * Translates numerical in-game indices into literal "chess-like" indices
     *
     * @return 'a', 'b', .., 'h' for successive columns (left-to-right)
     */
    public String strFile() {
        char[] x_c = new char[1];
        x_c[0] = (char) (97 + file);
        return new String(x_c);
    }

    /**
     * Translates numerical in-game indices into literal "chess-like" indices
     *
     * @return '1', '2', .., '8' for successive rows (bottom-up)
     */
    public String strRank() {
        return Integer.toString(8 - rank);
    }
}
