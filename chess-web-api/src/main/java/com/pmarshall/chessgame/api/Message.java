package com.pmarshall.chessgame.api;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.pmarshall.chessgame.api.endrequest.DrawProposition;
import com.pmarshall.chessgame.api.endrequest.DrawResponse;
import com.pmarshall.chessgame.api.endrequest.Surrender;
import com.pmarshall.chessgame.api.lobby.LogIn;
import com.pmarshall.chessgame.api.lobby.MatchFound;
import com.pmarshall.chessgame.api.lobby.Ping;
import com.pmarshall.chessgame.api.move.Move;
import com.pmarshall.chessgame.api.move.OpponentMoved;
import com.pmarshall.chessgame.api.outcome.GameFinished;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type"
)
@JsonSubTypes({
        @Type(value = MatchFound.class, name = "MatchFound"),
        @Type(value = GameFinished.class, name = "GameFinished"),
        @Type(value = DrawProposition.class, name = "DrawProposition"),
        @Type(value = DrawResponse.class, name = "DrawResponse"),
        @Type(value = Surrender.class, name = "Surrender"),
        @Type(value = Move.class, name = "Move"),
        @Type(value = OpponentMoved.class, name = "OpponentMoved"),
        @Type(value = ChatMessage.class, name = "ChatMessage"),
        @Type(value = LogIn.class, name = "LogIn"),
        @Type(value = Ping.class, name = "Ping")
})
public interface Message {
}
