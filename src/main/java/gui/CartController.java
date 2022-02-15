package gui;

import game.Game;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import service.ServiceUser;
import user.Customer;

import java.net.URL;
import java.util.ResourceBundle;

/**
 *
 */
public class CartController implements Initializable {

    @FXML
    private Label labelAvailable, gamePrice, nameLabel;

    @FXML
    private Spinner<Integer> quantitySpinner;

    private final SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 1024);
    StoreController controller;
    private Game game;

    /**
     *
     */
    @FXML
    void remove() {
        if (!ServiceUser.removeFromCart((Customer) Main.user, game, ((Customer) Main.user).getShoppingCart().getCart().get(game)))
            return;

        controller.clickCart();
        controller.updateLabels();
    }

    /**
     *
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        quantitySpinner.valueProperty().addListener((property, oldQuantity, newQuantity) -> {
            if (oldQuantity != null) {
                if (oldQuantity < newQuantity) {
                    if (!ServiceUser.addToCart((Customer) Main.user, game, newQuantity - oldQuantity))
                        valueFactory.setValue(oldQuantity);
                } else if (oldQuantity > newQuantity) {
                    if (!ServiceUser.removeFromCart((Customer) Main.user, game, oldQuantity - newQuantity))
                        valueFactory.setValue(oldQuantity);
                }

                gamePrice.setText(game.getPrice() * newQuantity + " €");
                controller.updateLabels();
            }
        });
    }

    /**
     *
     */
    public void setData(Game game, StoreController controller){
        this.game = game;
        this.controller = controller;

        nameLabel.setText(game.getName());
        labelAvailable.setText(game.getStatus() ? "Available" : "Unavailable");

        int quantity = ((Customer)Main.user).getShoppingCart().getCart().get(game);
        valueFactory.setValue(quantity);
        quantitySpinner.setValueFactory(valueFactory);
        gamePrice.setText(game.getPrice() * quantity + " €");
    }
}
