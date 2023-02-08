module com.pmarshall.chessgame.model {
    requires com.fasterxml.jackson.annotation;

    exports com.pmarshall.chessgame.model.properties;
    exports com.pmarshall.chessgame.model.dto;
    exports com.pmarshall.chessgame.model.service;
    exports com.pmarshall.chessgame.model.util;

    opens com.pmarshall.chessgame.model.properties to com.fasterxml.jackson.databind;
    opens com.pmarshall.chessgame.model.dto to com.fasterxml.jackson.databind;
}