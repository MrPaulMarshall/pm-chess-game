package com.pmarshall.chessgame.client.controller;

import com.pmarshall.chessgame.client.App;
import com.pmarshall.chessgame.client.FXMLUtils;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.util.function.Consumer;

public class UserNameDialogController {

    private final Consumer<String> resumeCallback;

    @FXML
    private TextField inputField;

    private UserNameDialogController(Consumer<String> resumeCallback) {
        this.resumeCallback = resumeCallback;
    }

    public static void askUserForName(Consumer<String> resumeCallback) {
        UserNameDialogController controller = new UserNameDialogController(resumeCallback);
        Parent root = FXMLUtils.load(controller, "/view/ask_for_player_name_screen.fxml");

        Stage stage = App.primaryStage();
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    private void initialize() {
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
