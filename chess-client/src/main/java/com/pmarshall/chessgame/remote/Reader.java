package com.pmarshall.chessgame.remote;

import java.io.InputStream;

public class Reader extends Thread {

    private final InputStream in;

    public Reader(InputStream in) {
        this.in = in;
    }

    @Override
    public void run() {

    }

}
