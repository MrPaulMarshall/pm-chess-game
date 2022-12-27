package com.marshall.chessgame.server.domain;

public class ConnectionDeadException extends Exception {

    private final String playerId;

    public ConnectionDeadException(String playerId) {
        this.playerId = playerId;
    }

    public String getPlayerId() {
        return playerId;
    }
}
