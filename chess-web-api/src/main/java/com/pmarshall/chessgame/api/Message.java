package com.pmarshall.chessgame.api;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.pmarshall.chessgame.api.endrequest.DrawRequest;
import com.pmarshall.chessgame.api.endrequest.Surrender;
import com.pmarshall.chessgame.api.lobby.AssignId;
import com.pmarshall.chessgame.api.lobby.MatchFound;
import com.pmarshall.chessgame.api.move.request.Move;
import com.pmarshall.chessgame.api.move.request.PromotionDecision;
import com.pmarshall.chessgame.api.move.response.MoveAccepted;
import com.pmarshall.chessgame.api.move.response.MoveRejected;
import com.pmarshall.chessgame.api.outcome.GameOutcome;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type"
)
@JsonSubTypes({
        @Type(value = AssignId.class, name = "AssignId"),
        @Type(value = MatchFound.class, name = "MatchFound"),
        @Type(value = GameOutcome.class, name = "GameOutcome"),
        @Type(value = DrawRequest.class, name = "DrawRequest"),
        @Type(value = Surrender.class, name = "Surrender"),
        @Type(value = Move.class, name = "Move"),
        @Type(value = PromotionDecision.class, name = "PromotionDecision"),
        @Type(value = MoveAccepted.class, name = "MoveAccepted"),
        @Type(value = MoveRejected.class, name = "MoveRejected")
})
public interface Message {
}
