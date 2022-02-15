package gui;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import order.Order;
import service.ServiceUser;
import user.Customer;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 *
 */
public class PopUpController implements Initializable {
    @FXML
    private Label labelPrice, labelGames, labelText;

    @FXML
    private ImageView imagePopUp;

    @FXML
    private Button buttonPopUp, buttonRejectLogout, buttonConfirmLogout;

    /**
     *
     */
    @FXML
    void revert(MouseEvent event) {
        ((Stage)((Node)event.getSource()).getScene().getWindow()).close();
    }

    /**
     *
     */
    @FXML
    void logout(MouseEvent event){
        try {
            Main.root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/login.fxml")));
        } catch (IOException e) {
            return;
        }

        revert(event);

        ServiceUser.logout(Main.user);

        Scene scene = new Scene(Main.root);

        Main.dragStage();
        Main.stage.setScene(scene);
        Main.stage.show();
        Main.centerStage();
    }

    /**
     *
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        labelPrice.setText("(" + ((Customer)Main.user).getShoppingCart().getTotalPrice() + " €)");
        labelGames.setText(((Customer)Main.user).getShoppingCart().getCart().size() + " GAME(S) IN THE CART");
    }

    /**
     *
     */
    public void setMode(int mode, Order order){
        switch (mode) {
            case 0 -> {
                labelText.setText("Item added to your cart");
                imagePopUp.setImage(new Image("file:src/main/resources/images/checked.png"));
                buttonPopUp.setStyle("-fx-border-color: #85d787; -fx-text-fill: #85d787;");
            }
            case 1 -> {
                labelText.setText("Item removed from your cart");
                imagePopUp.setImage(new Image("file:src/main/resources/images/removed.png"));
                buttonPopUp.setStyle("-fx-border-color: #f37e98; -fx-text-fill: #f37e98;");
            }
            case 2 -> {
                labelText.setText("Your order has been processed");
                imagePopUp.setImage(new Image("file:src/main/resources/images/checked.png"));
                buttonPopUp.setStyle("-fx-border-color: #85d787; -fx-text-fill: #85d787;");
                labelPrice.setText("(" + order.getTotalPrice() + " €)");
                labelGames.setText(order.getOrder().size() + " UNIQUE GAME(S) IN YOUR ORDER");
            }
            case 3 -> {
                labelText.setText("Your order has not been completed");
                imagePopUp.setImage(new Image("file:src/main/resources/images/removed.png"));
                buttonPopUp.setStyle("-fx-border-color: #f37e98; -fx-text-fill: #f37e98;");
                labelPrice.setText("");
                labelGames.setText("One or more games have become unavailable");
            }
            case 4 -> {
                labelText.setText("Are you sure you want to log out?");
                imagePopUp.setImage(new Image("file:src/main/resources/images/warning.png"));
                imagePopUp.setFitWidth(100);
                imagePopUp.setFitHeight(100);
                imagePopUp.setLayoutX(250);
                buttonPopUp.setStyle("-fx-border-color: #f37e98; -fx-text-fill: #f37e98;");
                labelPrice.setText("");
                labelGames.setText("");
                buttonPopUp.setVisible(false);
                buttonConfirmLogout.setVisible(true);
                buttonRejectLogout.setVisible(true);
            }
            case 5 -> {
                labelText.setText("Your password has been changed.");
                labelGames.setVisible(false);
                labelPrice.setVisible(false);
                imagePopUp.setImage(new Image("file:src/main/resources/images/checked.png"));
                buttonPopUp.setStyle("-fx-border-color: #85d787; -fx-text-fill: #85d787;");
                buttonPopUp.setText("Continue");
            }
            case 6 -> {
                labelText.setText("Your details have been updated.");
                labelGames.setVisible(false);
                labelPrice.setVisible(false);
                imagePopUp.setImage(new Image("file:src/main/resources/images/checked.png"));
                buttonPopUp.setStyle("-fx-border-color: #85d787; -fx-text-fill: #85d787;");
                buttonPopUp.setText("Continue");
            }
        }

    }

    /**
     *
     */
    public static void setUp(Parent root, Stage stage, AnchorPane anchorPane, Event event){
        Stage parentStage = (Stage)((Node)event.getSource()).getScene().getWindow();

        stage.setScene(new Scene(root, Color.TRANSPARENT));
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(((Node) event.getSource()).getScene().getWindow());

        anchorPane.setDisable(true);

        stage.setX(parentStage.getX() + 375);
        stage.setY(parentStage.getY() + 175);

        stage.showAndWait();
        anchorPane.setDisable(false);
    }
}