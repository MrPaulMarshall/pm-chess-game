package com.pmarshall.chessgame.api

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.pmarshall.chessgame.api.endrequest.DrawRequest
import com.pmarshall.chessgame.api.endrequest.Surrender
import com.pmarshall.chessgame.api.lobby.AssignId
import com.pmarshall.chessgame.api.lobby.MatchFound
import com.pmarshall.chessgame.api.move.request.Move
import com.pmarshall.chessgame.api.move.request.PromotionDecision
import com.pmarshall.chessgame.api.move.response.MoveAccepted
import com.pmarshall.chessgame.api.move.response.MoveRejected
import com.pmarshall.chessgame.api.outcome.GameOutcome
import com.pmarshall.chessgame.model.pieces.PieceType
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
        '{"type":"DrawRequest","action":"PROPOSE"}'                | new DrawRequest(DrawRequest.Action.PROPOSE)
        '{"type":"Surrender"}'                                     | new Surrender()
        '{"type":"AssignId","id":"abc123"}'                        | new AssignId('abc123')
        '{"type":"MatchFound","opponentId":"a1","color":"WHITE"}'  | new MatchFound('a1', Color.WHITE)
        '{"type":"Move","from":{"x":1,"y":4},"to":{"x":5,"y":4}}'  | new Move(new Position(1,4), new Position(5,4))
        '{"type":"PromotionDecision","decision":"KNIGHT"}'         | new PromotionDecision(PieceType.KNIGHT)
        '{"type":"MoveAccepted","promotionRequired":true}'         | new MoveAccepted(true)
        '{"type":"MoveRejected"}'                                  | new MoveRejected()
        '{"type":"GameOutcome","outcome":"DEFEAT","message":null}' | new GameOutcome(GameOutcome.Type.DEFEAT, null)
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
                '{"type":"DrawRequest","action":"propose"}'
        ]
    }
}
