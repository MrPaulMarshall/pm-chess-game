package com.pmarshall.chessgame.model.properties;

/**
 * @author Paweł Marszał
 * <p>
 * Class representing position on the board
 * file - column
 * rank - row
 * <p>
 * They map from in-game coordinates into chess-like coordinates as follows:
 * file: {0, 1, .., 7} -> {'a', 'b', .., 'h'}
 * rank: {0, 1, .., 7} -> {'8', '7', .., '1'}
 */
public record Position(int file, int rank) {

    /**
     * Translates numerical in-game indices into literal "chess-like" indices
     *
     * @return 'a', 'b', .., 'h' for successive columns (left-to-right)
     */
    public String strFile() {
        char[] x_c = new char[1];
        x_c[0] = (char) (97 + this.file);
        return new String(x_c);
    }

    /**
     * Translates numerical in-game indices into literal "chess-like" indices
     *
     * @return '1', '2', .., '8' for successive rows (bottom-up)
     */
    public String strRank() {
        return Integer.toString(8 - this.rank);
    }
}
