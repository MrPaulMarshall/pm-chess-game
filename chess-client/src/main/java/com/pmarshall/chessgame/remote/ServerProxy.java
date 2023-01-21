package com.pmarshall.chessgame.remote;

import com.pmarshall.chessgame.model.properties.Color;

public interface ServerProxy {

    Color localPlayer();

    void surrender() throws InterruptedException;

    void proposeDraw() throws InterruptedException;
    void acceptDraw() throws InterruptedException;
    void rejectDraw() throws InterruptedException;

//    void terminateGame(); TODO: it should be private method of RemoteGameProxy

}
