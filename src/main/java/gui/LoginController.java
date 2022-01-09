package gui;

import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;
import user.User;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import service.ServiceUser;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class LoginController {
    @FXML
    public Pane pnlRegister, pnlLogin;
    @FXML
    public Button btnLogin, btnSingUp;
    @FXML
    public TextField textEmail, textEmailRegister, textFirst, textLast, textPhone;
    @FXML
    public PasswordField textPassword, textPasswordRegister, textPasswordConfirm;
    @FXML
    public Label lblLogin, lblUser, lblPassword, lblAccount, lblSignUp, lblIncorrect, lblRegister, labelMessageRegister;
    @FXML
    public ImageView imgClose, imgClose1, imgBack, imgRepeat, imgUser, imgLock, imgContact, imageOk;

    private double xOffset = 0;
    private double yOffset = 0;

    public void switchStage(Event event) throws IOException {

        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/store.fxml")));
        Scene scene = new Scene(root);

        root.setOnMousePressed((javafx.scene.input.MouseEvent e) -> {
            xOffset = stage.getX() - e.getScreenX();
            yOffset = stage.getY() - e.getScreenY();
        });

        root.setOnMouseDragged((javafx.scene.input.MouseEvent e) ->{
            stage.setX(e.getScreenX() + xOffset);
            stage.setY(e.getScreenY() + yOffset);
        });

        stage.setScene(scene);

        stage.show();
        Main.centerStage(stage);
    }

    public void switchPanes(){
        if(pnlLogin.isVisible()){
            textFirst.setText("");
            textLast.setText("");
            textEmail.setText("");
            textPhone.setText("");
            textPassword.setText("");
            textPasswordConfirm.setText("");

            pnlLogin.setVisible(false);
            pnlRegister.setVisible(true);
        }
        else{
            textEmail.setText("");
            textPassword.setText("");

            pnlRegister.setVisible(false);
            lblIncorrect.setVisible(false);
            pnlLogin.setVisible(true);
        }
    }

    public void clickLogin(MouseEvent event) {
        String email = textEmail.getText();
        String password = textPassword.getText();
        User user = ServiceUser.login(email, password);

        if (user == null) {
            lblIncorrect.setText("Sorry, your email or password is incorrect.");
            lblIncorrect.setVisible(true);
        } else {
            try {
                Main.user = user;
                switchStage(event);
            } catch (IOException e) {
                lblIncorrect.setText("Unexpected error.");
                lblIncorrect.setVisible(true);

                ServiceUser.logout(user);
            }
        }
    }

    public void clickSignUp() {
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

        User user = ServiceUser.register(email, firstName, lastName, password, phoneNumber);

        if (user == null) {
            labelMessageRegister.setText("User with this email already exists.");
            labelMessageRegister.setVisible(true);
        } else {
            btnSingUp.setDisable(true);
            imageOk.setVisible(true);

            labelMessageRegister.setText("Registration completed!");
            labelMessageRegister.setFont(new Font("System", 18));
            labelMessageRegister.setTextFill(Color.web("#00c61a"));
            labelMessageRegister.setVisible(true);

            ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

            executorService.schedule(() -> {
                ServiceUser.logout(user);
                switchPanes();
            }, 2, TimeUnit.SECONDS);

        }
    }

    public void clickClose() {
        System.exit(0);
    }
}
