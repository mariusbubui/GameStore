package gui;

import game.Game;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.controlsfx.control.Rating;

/**
 *
 */
public class ItemController {
    @FXML
    private ImageView gameImage;

    @FXML
    private Label nameLabel, priceLabel, labelCategory;

    @FXML
    private Rating generalRating;

    /**
     *
     */
    @FXML
    private void clickGame(){
        listener.onClickListener(game);
    }

    /**
     *
     */
    @FXML
    public void cycleCategory(){
        if(game.getCategory().size() > 1){
            if(game.getCategory().size() == categoryIndex + 1)
                categoryIndex = 0;
            else
                categoryIndex++;

            labelCategory.setText(String.valueOf(game.getCategory().get(categoryIndex)));
        }
    }

    private Game game;
    private GameListener listener;
    private int categoryIndex = 0;

    /**
     *
     */
    public void setData(Game game, GameListener listener){
        this.game = game;
        this.listener = listener;
        gameImage.setImage(new Image("file:src/main/resources/game_covers/" + game.getImageName()));
        nameLabel.setText(game.getName());
        priceLabel.setText(game.getPrice() + " €");
        generalRating.setRating(game.getRating().getStars());
        labelCategory.setText(String.valueOf(game.getCategory().get(0)));
    }
}
