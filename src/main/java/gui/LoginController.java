package gui;

import exceptions.InvalidCodeException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import user.User;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import service.ServiceUser;

import java.io.IOException;
import java.util.Objects;

/**
 *
 */
public class LoginController {
    @FXML
    public Pane paneRegister, paneLogin, anchorPane;

    @FXML
    public Button btnLogin, btnSingUp;

    @FXML
    public TextField textEmail, textEmailRegister, textFirst, textLast, textPhone;

    @FXML
    public PasswordField textPassword, textPasswordRegister, textPasswordConfirm;


    @FXML
    public Label lblLogin, lblUser, lblPassword, lblAccount,
            lblSignUp, lblIncorrect, lblRegister, labelMessageRegister;

    @FXML
    public ImageView imgClose, imgClose1, imgBack,
            imgRepeat, imgUser, imgLock, imgContact, imageOk;

    private boolean confirmation;

    /**
     *
     */
    public void switchStage() throws IOException {
        Main.root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/store.fxml")));
        Scene scene = new Scene(Main.root);

        Main.dragStage();
        Main.stage.setScene(scene);
        Main.stage.show();
        Main.centerStage();
    }

    /**
     *
     */
    public void switchPanes(){
        if(paneLogin.isVisible()){
            textFirst.setText("");
            textLast.setText("");
            textEmail.setText("");
            textPhone.setText("");
            textPassword.setText("");
            textPasswordConfirm.setText("");

            paneLogin.setVisible(false);
            paneRegister.setVisible(true);
        }
        else{
            textEmail.setText("");
            textPassword.setText("");

            paneRegister.setVisible(false);
            lblIncorrect.setVisible(false);
            paneLogin.setVisible(true);
        }
    }

    /**
     *
     */
    public void login() {
        String email = textEmail.getText();
        String password = textPassword.getText();
        User user = ServiceUser.login(email, password);

        if (user == null) {
            lblIncorrect.setText("Sorry, your email or password is incorrect.");
            lblIncorrect.setVisible(true);
        } else {
            try {
                Main.user = user;
                switchStage();
            } catch (IOException e) {
                lblIncorrect.setText("Unexpected error.");
                lblIncorrect.setVisible(true);

                ServiceUser.logout(user);
            }
        }
    }

    /**
     *
     */
    public void confirmation(Parent root,MouseEvent event){
        Stage stage = new Stage();
        Stage parentStage = (Stage)((Node)event.getSource()).getScene().getWindow();

        stage.setScene(new Scene(root, Color.TRANSPARENT));
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(anchorPane.getScene().getWindow());

        anchorPane.setDisable(true);

        stage.setX(parentStage.getX() + 100);
        stage.setY(parentStage.getY() + 50);
        stage.showAndWait();

        anchorPane.setDisable(false);
    }

    /**
     *
     */
    public void setConfirmation(boolean confirmation) {
        this.confirmation = confirmation;
    }

    /**
     *
     */
    public boolean getConfirmation() {
        return confirmation;
    }

    /**
     *
     */
    public void clickSignUp(MouseEvent event) {
        String email, password, confirmation, firstName, lastName, phoneNumber;
        email = textEmailRegister.getText();
        password = textPasswordRegister.getText();
        confirmation = textPasswordConfirm.getText();
        firstName = textFirst.getText();
        lastName = textLast.getText();
        phoneNumber = textPhone.getText();

        labelMessageRegister.setVisible(false);
        labelMessageRegister.setTextFill(Color.web("#ff0505"));
        labelMessageRegister.setFont(new Font("System", 14));
        btnSingUp.setDisable(false);

        if (firstName.matches("^[a-zA-Z]{2,}$")) {
            firstName = firstName.substring(0, 1).toUpperCase() + firstName.substring(1).toLowerCase();
        } else {
            labelMessageRegister.setText("First name is invalid.");
            labelMessageRegister.setVisible(true);
            return;
        }

        if (lastName.matches("^[a-zA-Z]{2,}$")) {
            lastName = lastName.substring(0, 1).toUpperCase() + lastName.substring(1).toLowerCase();
        } else {
            labelMessageRegister.setText("Last name is invalid.");
            labelMessageRegister.setVisible(true);
            return;
        }

        if (email.matches("^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")) {
            email = email.toLowerCase();
        } else {
            labelMessageRegister.setText("Email address is invalid.");
            labelMessageRegister.setVisible(true);
            return;
        }

        if (!phoneNumber.matches("^[0-9]{5,}$")) {
            labelMessageRegister.setText("Phone number is invalid.");
            labelMessageRegister.setVisible(true);
            return;
        }

        if(!password.matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$")){
            labelMessageRegister.setText("Password must contain minimum 8 characters,\n       at least one letter and one number.");
            labelMessageRegister.setVisible(true);
            return;
        }

        if (!password.equals(confirmation)) {
            labelMessageRegister.setText("The passwords do not match.");
            labelMessageRegister.setVisible(true);
            return;
        }

        Parent root;
        User user;
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/view/confirmation.fxml")));

        try {
            root = loader.load();

            user = ServiceUser.register(email, firstName, lastName, password,
                    phoneNumber, root, loader.getController(), this, event);

            if (user == null) {
                labelMessageRegister.setText("User with this email already exists.");
                labelMessageRegister.setVisible(true);
            } else {
                btnSingUp.setDisable(true);
                ServiceUser.logout(user);
                switchPanes();
            }

        } catch (IOException e) {
            labelMessageRegister.setText("Unexpected error.");
            labelMessageRegister.setVisible(true);
        } catch (InvalidCodeException e) {
            labelMessageRegister.setText("Please try again.");
            labelMessageRegister.setVisible(true);
        }
    }

    /**
     *
     */
    public void clickClose() {
        System.exit(0);
    }
}
