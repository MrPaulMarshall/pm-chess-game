package com.pmarshall.chessgame.server;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

public record PlayerConnection(
        String id,
        String name,
        InputStream in,
        OutputStream out
) {

    public PlayerConnection {
        Objects.requireNonNull(id);
        Objects.requireNonNull(name);
        Objects.requireNonNull(in);
        Objects.requireNonNull(out);
    }
}
