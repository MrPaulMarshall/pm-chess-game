package com.pmarshall.chessgame.api;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.pmarshall.chessgame.api.request.DrawRequest;
import com.pmarshall.chessgame.api.request.InvitationRequest;
import com.pmarshall.chessgame.api.response.InvitationResponse;
import com.pmarshall.chessgame.api.request.SurrenderRequest;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type"
)
@JsonSubTypes({
        @Type(value = InvitationRequest.class, name = "IR"),
        @Type(value = SurrenderRequest.class, name = "SR"),
        @Type(value = InvitationResponse.class, name = "IA"),
        @Type(value = DrawRequest.class, name = "DR")
})
public interface Message {
}
