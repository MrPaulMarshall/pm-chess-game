package com.pmarshall.chessgame.model.dto;

import com.pmarshall.chessgame.model.properties.Position;

import java.util.Objects;

public record LegalMove(
        Position from,
        Position to,
        boolean promotion,
        boolean withCheck,
        String notation
) {

    public LegalMove {
        Objects.requireNonNull(from);
        Objects.requireNonNull(to);
        Objects.requireNonNull(notation);
    }

    /**
     * Two moves are considered equal, when they are made from-to the same positions
     * and the promotion requirements (or lack of it) are the same.
     */
    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof LegalMove other) {
            return this.from.equals(other.from) && this.to.equals(other.to);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to);
    }
}
