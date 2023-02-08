package com.pmarshall.chessgame.model.util;

import java.util.Objects;

public record Triple<L, M, R>(L left, M mid, R right) {

    public Triple {
        Objects.requireNonNull(left);
        Objects.requireNonNull(mid);
        Objects.requireNonNull(right);
    }

    public static <L, M, R> Triple<L, M, R> of(L left, M mid, R right) {
        return new Triple<>(left, mid, right);
    }
}
