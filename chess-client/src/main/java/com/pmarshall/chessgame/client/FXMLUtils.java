package com.pmarshall.chessgame.client;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class FXMLUtils {

    private static final Logger log = LoggerFactory.getLogger(FXMLUtils.class);

    public static <T> Parent load(T controller, String resource) {
        try {
            FXMLLoader loader = new FXMLLoader(controller.getClass().getResource(resource));
            loader.setControllerFactory(clazz -> controller);
            return loader.load();
        } catch (IOException e) {
            log.error("Could not load view for {} from {}", controller.getClass().getSimpleName(), resource, e);
            System.exit(1);
            return null;
        }
    }
}
