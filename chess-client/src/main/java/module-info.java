module com.pmarshall.chessgame.client {
    requires javafx.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires org.slf4j;

    requires com.pmarshall.chessgame.api;
    requires com.pmarshall.chessgame.model;

    uses com.pmarshall.chessgame.model.service.Game;

    exports com.pmarshall.chessgame.client.controller to javafx.fxml;

    opens com.pmarshall.chessgame.client to javafx.graphics;
    opens com.pmarshall.chessgame.client.controller to javafx.fxml;

    opens images;
}