module com.pmarshall.chessgame.engine {
    requires com.pmarshall.chessgame.model;

    provides com.pmarshall.chessgame.model.service.Game
            with com.pmarshall.chessgame.engine.game.InMemoryChessGame;
}