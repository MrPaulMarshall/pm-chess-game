module com.pmarshall.chessgame.api {
    requires com.fasterxml.jackson.annotation;
    requires transitive com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;

    requires com.pmarshall.chessgame.model;

    exports com.pmarshall.chessgame.api;
    exports com.pmarshall.chessgame.api.endrequest;
    exports com.pmarshall.chessgame.api.lobby;
    exports com.pmarshall.chessgame.api.move;
    exports com.pmarshall.chessgame.api.outcome;

    opens com.pmarshall.chessgame.api to com.fasterxml.jackson.databind;
    opens com.pmarshall.chessgame.api.endrequest to com.fasterxml.jackson.databind;
    opens com.pmarshall.chessgame.api.lobby to com.fasterxml.jackson.databind;
    opens com.pmarshall.chessgame.api.move to com.fasterxml.jackson.databind;
    opens com.pmarshall.chessgame.api.outcome to com.fasterxml.jackson.databind;
}