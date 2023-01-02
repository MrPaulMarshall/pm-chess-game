package com.pmarshall.chessgame.api.move.response;

public record MoveAccepted(boolean check, String moveRepresentation) implements MoveResponse {
}
