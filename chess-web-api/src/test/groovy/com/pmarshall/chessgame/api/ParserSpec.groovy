package com.pmarshall.chessgame.api

import com.fasterxml.jackson.core.JsonProcessingException
import com.pmarshall.chessgame.api.endrequest.DrawResponse
import com.pmarshall.chessgame.api.move.Move
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
        json                                                                       | object
        '{"type":"DrawResponse","accepted":true}'                                  | new DrawResponse(true)
        '{"type":"Move","from":{"file":1,"rank":4},"to":{"file":5,"rank":4},"promotion":null}' | new Move(new Position(1,4), new Position(5,4), null)
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
                '{"type":"DrawResponse","action":tRue}'
        ]
    }

    def 'should successfully deserialize part of the buffer'() {
        given:
        def bytes = '{"type":"DrawResponse","accepted":true}'.getBytes()
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
        message == new DrawResponse(true)
    }
}
