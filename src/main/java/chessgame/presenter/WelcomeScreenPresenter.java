package chessgame.presenter;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.stage.Stage;

public class WelcomeScreenPresenter {

    private Stage dialogStage;

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }


    @FXML
    public void initialize() {

    }


    @FXML
    public void handleOkAction(ActionEvent e) {
        dialogStage.close();
    }

    @FXML
    public void handleRegisterAction(ActionEvent e) {

    }

    @FXML
    public void handleCancelAction(ActionEvent e) {
        dialogStage.close();
        System.exit(0);
    }

}
