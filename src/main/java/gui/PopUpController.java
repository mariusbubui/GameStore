package gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import user.Customer;

import java.net.URL;
import java.util.ResourceBundle;

public class PopUpController implements Initializable {
    @FXML
    private Label labelPrice, labelGames, labelText;

    @FXML
    private ImageView imagePopUp;

    @FXML
    private Button buttonPopUp;

    @FXML
    void revert(MouseEvent event) {
        ((Stage)((Node)event.getSource()).getScene().getWindow()).close();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        labelPrice.setText("(" + ((Customer)Main.user).getShoppingCart().getTotalPrice() + " €)");
        labelGames.setText(((Customer)Main.user).getShoppingCart().getCart().size() + " GAME(S) IN THE CART");
    }

    public void setMode(boolean mode){
        if(mode) {
            labelText.setText("Item added to your cart");
            imagePopUp.setImage(new Image("file:src/main/resources/images/checked.png"));
            buttonPopUp.setStyle("-fx-border-color: #85d787; -fx-text-fill: #85d787;");
        }
        else{
            labelText.setText("Item removed from your cart");
            imagePopUp.setImage(new Image("file:src/main/resources/images/removed.png"));
            buttonPopUp.setStyle("-fx-border-color: #f37e98; -fx-text-fill: #f37e98;");
        }
    }
}