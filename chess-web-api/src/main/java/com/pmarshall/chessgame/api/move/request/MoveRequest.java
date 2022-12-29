package com.pmarshall.chessgame.api.move.request;

import com.pmarshall.chessgame.api.Message;
import com.pmarshall.chessgame.model.properties.Position;

public interface MoveRequest extends Message {
    Position from();
    Position to();
}
