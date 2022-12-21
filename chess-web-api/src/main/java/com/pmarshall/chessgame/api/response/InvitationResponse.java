package com.pmarshall.chessgame.api.response;

import com.pmarshall.chessgame.api.Message;

import java.util.Objects;

public record InvitationResponse(
        String inviterId,
        String inviteeId,
        boolean accepted
) implements Message {

    public InvitationResponse {
        Objects.requireNonNull(inviterId);
        Objects.requireNonNull(inviteeId);
    }
}
