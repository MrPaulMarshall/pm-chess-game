package com.marshall.chessgame.server.domain;

import java.net.Socket;

public record PlayerConnection(
        String playerId,
        Socket socket,
        Thread reader,
        Thread writer
) {
}
