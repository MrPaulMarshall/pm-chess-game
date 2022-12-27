package com.marshall.chessgame.server.domain;

import java.net.Socket;
import java.util.Objects;

public record PlayerConnection(
        String id,
        Socket socket
) {

    public PlayerConnection {
        Objects.requireNonNull(id);
        Objects.requireNonNull(socket);
    }
}
