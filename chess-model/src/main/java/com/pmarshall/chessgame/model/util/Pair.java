package com.pmarshall.chessgame.model.util;

import java.util.Objects;

public record Pair<K, V>(K left, V right) {

    public Pair {
        Objects.requireNonNull(left);
        Objects.requireNonNull(right);
    }

    public static <K, V> Pair<K, V> of(K left, V right) {
        return new Pair<>(left, right);
    }
}
