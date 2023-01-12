package com.pmarshall.chessgame.api.endrequest;

import com.pmarshall.chessgame.api.Message;

public record DrawResponse(boolean accepted) implements Message {
}
