package gui;

import game.Game;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.input.MouseEvent;
import user.Customer;

public class CartController {

    @FXML
    private Label labelAvailable, gamePrice, nameLabel;

    @FXML
    private Spinner<Integer> quantitySpinner;

    private final SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 1024);
    private Game game;

    @FXML
    void remove(MouseEvent event) {

    }

    public void setData(Game game){
        this.game = game;
        nameLabel.setText(game.getName());
        gamePrice.setText(game.getPrice() + " €");
        labelAvailable.setText(game.getStatus() ? "Available" : "Unavailable");
        valueFactory.setValue(((Customer)Main.user).getShoppingCart().getCart().get(game));
        quantitySpinner.setValueFactory(valueFactory);
    }

}
