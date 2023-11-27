module com.pmarshall.chessgame.client {
    requires com.pmarshall.chessgame.api;
    requires com.pmarshall.chessgame.model;
    requires javafx.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires org.slf4j;

    exports com.pmarshall.chessgame.client.controller to javafx.fxml;

    uses com.pmarshall.chessgame.model.service.Game;

    opens com.pmarshall.chessgame.client to javafx.graphics;
    opens com.pmarshall.chessgame.client.controller to javafx.fxml;
    opens images;
    opens icons;
}