package com.pmarshall.chessgame.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pmarshall.chessgame.api.request.DrawRequest;
import com.pmarshall.chessgame.api.response.InvitationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * General tests for building and parsing message objects and JSONs.
 * Most of the tests are made on the representative InvitationResponse type,
 *  because it has both String and non-String properties.
 * More complicated message Types (like MoveResponse) can have their own specific tests too.
 */
class ApiMessageTest {
    private final ObjectMapper objectMapper = new ObjectMapper();

    private String jsonFormat;
    private String type;
    private String inviterId;
    private String inviteeId;
    private boolean accepted;

    @BeforeEach
    public void setUp() {
        this.jsonFormat = "{\"type\":\"%s\",\"inviterId\":\"%s\",\"inviteeId\":\"%s\",\"accepted\":%b}";
        this.type = "IA";
        this.inviterId = "player-1";
        this.inviteeId = "player-2";
        this.accepted = true;
    }

    @Test
    public void shouldNotBeAbleToBuildMessageWithNullValues() {
        assertThatThrownBy(() -> new InvitationResponse(this.inviterId, null, false))
                .isInstanceOf(NullPointerException.class);

        assertThatThrownBy(() -> new InvitationResponse(null, this.inviteeId, false))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    public void shouldMapJsonToInvitationResponse() throws JsonProcessingException {
        // given
        String json = String.format(this.jsonFormat, this.type, this.inviterId, this.inviteeId, this.accepted);

        // when
        InvitationResponse message = this.objectMapper.readerFor(Message.class).readValue(json);

        // then
        assertThat(message.inviterId()).isEqualTo(this.inviterId);
        assertThat(message.inviteeId()).isEqualTo(this.inviteeId);
        assertThat(message.accepted()).isEqualTo(this.accepted);
    }

    @Test
    public void shouldMapJsonToDrawRequest() throws JsonProcessingException {
        // given
        this.jsonFormat = "{\"type\":\"%s\",\"gameSessionId\":\"%s\",\"proponentId\":\"%s\"}";
        String gameSessionId = "game-session-id";
        String json = String.format(this.jsonFormat, "DR", gameSessionId, this.inviterId);

        // when
        DrawRequest message = this.objectMapper.readerFor(Message.class).readValue(json);

        // then
        assertThat(message.gameSessionId()).isEqualTo(gameSessionId);
        assertThat(message.proponentId()).isEqualTo(this.inviterId);
    }

    @Test
    public void shouldThrowExceptionWhenTypeIsUnknown() {
        // given
        String json = String.format(this.jsonFormat, "Unknown", this.inviterId, this.inviteeId, this.accepted);

        // when-then
        assertThatThrownBy(() -> this.objectMapper.readerFor(Message.class).readValue(json))
                .isInstanceOf(JsonProcessingException.class);
    }

    @Test
    public void shouldMapMessageToJson() throws JsonProcessingException {
        // given
        InvitationResponse message = new InvitationResponse(this.inviterId, this.inviteeId, this.accepted);

        // when
        String json = this.objectMapper.writeValueAsString(message);

        // then
        String expected = String.format(this.jsonFormat, this.type, this.inviterId, this.inviteeId, this.accepted);
        assertThat(json).isEqualTo(expected);
    }

    @Test
    public void shouldNotAcceptJsonWithNullValues() {
        // given
        this.jsonFormat = "{\"type\":\"%s\",\"inviterId\":\"%s\",\"inviteeId\":null,\"accepted\":%b}";
        String json = String.format(this.jsonFormat, this.type, this.inviterId, this.accepted);

        // when-then
        assertThatThrownBy(() -> this.objectMapper.readerFor(Message.class).readValue(json))
                .isInstanceOf(JsonProcessingException.class);
    }

    @Test
    public void shouldNotAcceptJsonWithMissingProperties() {
        // given
        this.jsonFormat = "{\"type\":\"%s\",\"inviteeId\":\"%s\",\"accepted\":%b}";
        String json = String.format(this.jsonFormat, this.type, this.inviteeId, this.accepted);

        // when-then
        assertThatThrownBy(() -> this.objectMapper.readerFor(Message.class).readValue(json))
                .isInstanceOf(JsonProcessingException.class);
    }
}
