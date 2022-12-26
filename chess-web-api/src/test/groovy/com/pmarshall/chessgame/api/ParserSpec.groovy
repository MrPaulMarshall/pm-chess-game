package com.pmarshall.chessgame.api

import com.fasterxml.jackson.core.JsonProcessingException
import com.pmarshall.chessgame.api.endrequest.DrawRequest
import com.pmarshall.chessgame.api.move.request.Move
import com.pmarshall.chessgame.model.properties.Position
import spock.lang.Specification

import java.nio.charset.StandardCharsets

class ParserSpec extends Specification {

    def 'should successfully parse correct message to and from byte array'() throws JsonProcessingException {
        given:
        def bytes = json.getBytes(StandardCharsets.UTF_8)
        def message = object

        when:
        def parsedBytes = Parser.serialize(message)
        def parsedObject = Parser.deserialize(bytes, bytes.length)

        then:
        Arrays.equals(parsedBytes, bytes)
        parsedObject == object

        where:
        json                                                      | object
        '{"type":"DrawRequest","action":"PROPOSE"}'               | new DrawRequest(DrawRequest.Action.PROPOSE)
        '{"type":"Move","from":{"x":1,"y":4},"to":{"x":5,"y":4}}' | new Move(new Position(1,4), new Position(5,4))
    }

    def 'should reject invalid messages'() {
        given:
        def bytes = json.getBytes(StandardCharsets.UTF_8)

        when:
        Parser.deserialize(bytes, bytes.length)

        then:
        thrown JsonProcessingException

        where:
        json << [
                '',
                '{}',
                'null',
                '{"type":"DrawRequest","action":"propose"}'
        ]
    }

    def 'should successfully deserialize part of the buffer'() {
        given:
        def bytes = '{"type":"DrawRequest","action":"PROPOSE"}'.getBytes()
        def buffer = new byte[bytes.length + 20]
        // fill with useful data
        for (int i = 0; i < bytes.length; i++) {
            buffer[i] = bytes[i]
        }
        // some garbage we're not interested in
        for (int i = bytes.length; i < bytes.length + 20; i++) {
            buffer[i] = (byte) i
        }

        when:
        def message = Parser.deserialize(buffer, bytes.length)

        then:
        message == new DrawRequest(DrawRequest.Action.PROPOSE)
    }
}
