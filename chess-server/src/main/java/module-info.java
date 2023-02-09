module com.pmarshall.chessgame.server {
    requires com.pmarshall.chessgame.model;
    requires com.pmarshall.chessgame.api;
    requires org.slf4j;

    uses com.pmarshall.chessgame.model.service.Game;
}