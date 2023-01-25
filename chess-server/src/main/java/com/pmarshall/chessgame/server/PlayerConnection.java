package com.pmarshall.chessgame.server;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

public record PlayerConnection(
        String id,
        InputStream in,
        OutputStream out
) {

    public PlayerConnection {
        Objects.requireNonNull(id);
        Objects.requireNonNull(in);
        Objects.requireNonNull(out);
    }
}
