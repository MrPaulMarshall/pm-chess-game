package com.pmarshall.chessgame.remote;

import java.io.OutputStream;

public class Writer extends Thread {

    private final OutputStream out;

    public Writer(OutputStream out) {
        this.out = out;
    }

    @Override
    public void run() {

    }

}
