package com.pmarshall.chessgame.client.remote;

import com.pmarshall.chessgame.model.properties.Color;

public interface ServerProxy {

    Color localPlayer();
    void surrender() throws InterruptedException;
    void proposeDraw() throws InterruptedException;
    void acceptDraw() throws InterruptedException;
    void rejectDraw() throws InterruptedException;
}
