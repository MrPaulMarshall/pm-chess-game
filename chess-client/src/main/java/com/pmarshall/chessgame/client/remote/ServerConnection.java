package com.pmarshall.chessgame.client.remote;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Objects;

public record ServerConnection(
        Socket socket,
        InputStream in,
        OutputStream out
) {

    public ServerConnection {
        Objects.requireNonNull(socket);
        Objects.requireNonNull(in);
        Objects.requireNonNull(out);
    }
}
