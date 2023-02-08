module com.pmarshall.chessgame.server {
    requires org.slf4j;

    requires com.pmarshall.chessgame.model;
    requires com.pmarshall.chessgame.api;

    uses com.pmarshall.chessgame.model.service.Game;
}