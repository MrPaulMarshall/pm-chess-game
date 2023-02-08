package com.pmarshall.chessgame.api

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.pmarshall.chessgame.api.endrequest.DrawProposition
import com.pmarshall.chessgame.api.endrequest.DrawResponse
import com.pmarshall.chessgame.api.endrequest.Surrender
import com.pmarshall.chessgame.api.lobby.AssignId
import com.pmarshall.chessgame.api.lobby.MatchFound
import com.pmarshall.chessgame.api.move.Move
import com.pmarshall.chessgame.api.outcome.GameOutcome
import com.pmarshall.chessgame.model.properties.PieceType
import com.pmarshall.chessgame.model.properties.Color
import com.pmarshall.chessgame.model.properties.Position
import spock.lang.Specification

class MessageSpec extends Specification {

    private static ObjectMapper mapper = new ObjectMapper()

    def 'should correctly parse json to object and vice versa'() {
        when:
        def parsedObject = mapper.readValue(json, Message.class)
        def parsedJson = mapper.writeValueAsString(object)

        then:
        parsedObject == object
        and:
        parsedJson == json

        where:
        json                                                       | object
        'null'                                                     | null
        '{"type":"DrawProposition"}'                               | new DrawProposition()
        '{"type":"DrawResponse","accepted":false}'                 | new DrawResponse(false)
        '{"type":"Surrender"}'                                     | new Surrender()
        '{"type":"AssignId","id":"abc123"}'                        | new AssignId('abc123')
        '{"type":"GameOutcome","outcome":"DEFEAT","message":null}' | new GameOutcome(GameOutcome.Type.DEFEAT, null)
        '{"type":"Move","from":{"rank":1,"file":4},"to":{"rank":5,"file":4},"promotion":null}'     | new Move(new Position(1,4), new Position(5,4), null)
        '{"type":"Move","from":{"rank":1,"file":4},"to":{"rank":5,"file":4},"promotion":"KNIGHT"}' | new Move(new Position(1,4), new Position(5,4), PieceType.KNIGHT)
        '{"type":"MatchFound","color":"BLACK","opponentId":"a1","legalMoves":[]}'  | new MatchFound(Color.BLACK, 'a1', List.of())
    }

    def 'should throw JsonProcessingException when given invalid json'() {
        when:
        mapper.readValue(json, Message.class)

        then:
        thrown JsonProcessingException

        where:
        json << [
                '',
                '{}',
                '{"type":"unknown"}',
                '{"type":"AssignId","id":null}',
                '{"type":"DrawResponse","accepted":"string"}'
        ]
    }
}
