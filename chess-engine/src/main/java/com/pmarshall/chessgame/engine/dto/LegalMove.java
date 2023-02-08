package com.pmarshall.chessgame.engine.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.pmarshall.chessgame.engine.properties.Position;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type"
)
@JsonSubTypes({
        @Type(value = DefaultMove.class, name = "DefaultMove"),
        @Type(value = Promotion.class, name = "Promotion"),
        @Type(value = EnPassant.class, name = "EnPassant"),
        @Type(value = Castling.class, name = "Castling")
})
public interface LegalMove {
    Position from();
    Position to();
    boolean check();
    String notation();
}
