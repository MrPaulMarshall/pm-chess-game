package com.pmarshall.chessgame.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;

public final class Parser {

    private static final ObjectMapper jackson = new ObjectMapper();

    public static Message deserialize(byte[] data, int length) throws JsonProcessingException {
        String json = new String(data, 0, length, StandardCharsets.UTF_8);
        Message msg = jackson.readValue(json, Message.class);
        if (msg == null)
            throw new JsonProcessingException("Json \"null\" is not allowed") {};
        return msg;
    }

    public static byte[] serialize(Message msg) {
        try {
            String json = jackson.writeValueAsString(msg);
            return json.getBytes(StandardCharsets.UTF_8);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException(
                    "This exception should never have happened, because the Message objects are serializable", ex);
        }
    }

    public static byte[] serializeLength(int length) {
        byte[] buffer = new byte[2];
        buffer[0] = (byte) (length & 0xFF);
        buffer[1] = (byte) ((length >> 8) & 0xFF);
        return buffer;
    }

    public static int deserializeLength(byte[] buffer) {
        int length = 0;
        length += buffer[0] & 0xFF;
        length += ((int) buffer[1] & 0xFF) << 8;
        return length;
    }
}
