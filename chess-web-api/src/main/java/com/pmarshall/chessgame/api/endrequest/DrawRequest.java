package com.pmarshall.chessgame.api.drawdialog;

import com.pmarshall.chessgame.api.Message;

import java.util.Objects;

public record DrawRequest(
        DrawAction action
) implements Message {

    public DrawRequest {
        Objects.requireNonNull(action);
    }
}
