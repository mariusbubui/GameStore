package gui;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

/**
 *
 */
public class ConfirmationController {

    @FXML
    private Button buttonConfirm;

    @FXML
    private ImageView imageConfirmation;

    @FXML
    private Label labelText;

    @FXML
    private TextField textConfirmation;

    private String code;
    private LoginController controller;

    /**
     *
     */
    @FXML
    private void verify(MouseEvent event) {
        if(buttonConfirm.getText().equals("Verify")){
            if (!textConfirmation.getText().equals("")) {
                buttonConfirm.setText("Continue");
                textConfirmation.setVisible(false);

                if(textConfirmation.getText().equals(code)){
                    labelText.setText("Verification is complete.");
                    controller.setConfirmation(true);
                } else {
                    imageConfirmation.setImage(new Image("file:src/main/resources/images/removed.png"));
                    labelText.setText("Invalid code inserted.\nPlease register again.");
                    buttonConfirm.setStyle("-fx-border-color: #f37e98; -fx-text-fill: #f37e98;");
                    controller.setConfirmation(false);
                }
            }
        } else {
            ((Stage)((Node)event.getSource()).getScene().getWindow()).close();
        }
    }

    /**
     *
     */
    public void setCode(String code, LoginController controller){
        this.code = code;
        this.controller = controller;
    }
}