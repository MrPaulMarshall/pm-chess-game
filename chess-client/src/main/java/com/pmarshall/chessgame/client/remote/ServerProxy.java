package com.pmarshall.chessgame.client.remote;

import com.pmarshall.chessgame.engine.properties.Color;

public interface ServerProxy {

    Color localPlayer();
    void surrender() throws InterruptedException;
    void proposeDraw() throws InterruptedException;
    void acceptDraw() throws InterruptedException;
    void rejectDraw() throws InterruptedException;
    void pushChatMessage(String message) throws InterruptedException;
}
