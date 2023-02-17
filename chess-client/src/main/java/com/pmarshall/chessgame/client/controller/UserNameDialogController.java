package com.pmarshall.chessgame.client.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.function.Consumer;

public class UserNameDialogController {

    private Consumer<String> resumeCallback;

    @FXML
    private TextField inputField;

    public static void askUserForName(Stage primaryStage, Consumer<String> resumeCallback) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(UserNameDialogController.class.getResource("/view/ask_for_player_name_screen.fxml"));
        Parent rootLayout = loader.load();
        UserNameDialogController controller = loader.getController();

        controller.injectDependencies(resumeCallback);

        Scene scene = new Scene(rootLayout);
        primaryStage.setTitle("Chess game");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void injectDependencies(Consumer<String> resumeCallback) {
        this.resumeCallback = resumeCallback;
    }

    @FXML
    public void initialize() {
        final int maxLength = 16;
        inputField.textProperty().addListener((ov, oldValue, newValue) -> {
            if (inputField.getText().length() > maxLength) {
                String s = inputField.getText().substring(0, maxLength);
                inputField.setText(s);
            }
        });
    }

    @FXML
    private void keyPressedHandler(KeyEvent event) {
        String userInput = inputField.getText().trim();
        if (event.getCode() == KeyCode.ENTER && userInput.length() > 0) {
            resumeCallback.accept(userInput);
        }
    }
}
