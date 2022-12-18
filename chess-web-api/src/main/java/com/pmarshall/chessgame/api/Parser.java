package com.pmarshall.chessgame.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;

public final class Parser {

    private static final ObjectMapper jackson = new ObjectMapper();

    public Message deserialize(byte[] data, int length) throws JsonProcessingException {
        String json = new String(data, 0, length, StandardCharsets.UTF_8);
        return jackson.readValue(json, Message.class);
    }

    public byte[] serialize(Message msg) {
        try {
            String json = jackson.writeValueAsString(msg);
            return json.getBytes(StandardCharsets.UTF_8);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException(
                    "This exception should never have happened, because the Message objects are serializable", ex);
        }
    }
}
