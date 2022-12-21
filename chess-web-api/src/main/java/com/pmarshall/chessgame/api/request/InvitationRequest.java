package com.pmarshall.chessgame.api.request;

import com.pmarshall.chessgame.api.Message;

import java.util.Objects;

public record InvitationRequest(
        String inviterId,
        String inviteeId
) implements Message {

    public InvitationRequest {
        Objects.requireNonNull(inviterId);
        Objects.requireNonNull(inviteeId);
    }
}
