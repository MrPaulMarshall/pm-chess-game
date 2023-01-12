package com.pmarshall.chessgame.api;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.pmarshall.chessgame.api.endrequest.DrawProposition;
import com.pmarshall.chessgame.api.endrequest.DrawResponse;
import com.pmarshall.chessgame.api.endrequest.Surrender;
import com.pmarshall.chessgame.api.lobby.AssignId;
import com.pmarshall.chessgame.api.lobby.MatchFound;
import com.pmarshall.chessgame.api.move.request.Move;
import com.pmarshall.chessgame.api.move.request.Promotion;
import com.pmarshall.chessgame.api.move.OpponentMoved;
import com.pmarshall.chessgame.api.outcome.GameOutcome;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type"
)
@JsonSubTypes({
        @Type(value = AssignId.class, name = "AssignId"),
        @Type(value = MatchFound.class, name = "MatchFound"),
        @Type(value = GameOutcome.class, name = "GameOutcome"),
        @Type(value = DrawProposition.class, name = "DrawProposition"),
        @Type(value = DrawResponse.class, name = "DrawResponse"),
        @Type(value = Surrender.class, name = "Surrender"),
        @Type(value = Move.class, name = "Move"),
        @Type(value = Promotion.class, name = "Promotion"),
        @Type(value = OpponentMoved.class, name = "OpponentMoved")
})
public interface Message {
}
