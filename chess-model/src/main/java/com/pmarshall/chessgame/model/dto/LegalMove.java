package com.pmarshall.chessgame.model.dto;

import com.pmarshall.chessgame.model.properties.Position;

public interface LegalMove {
    Position from();
    Position to();
}
