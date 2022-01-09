package gui;

import dao.GameDAO;
import game.Game;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.controlsfx.control.Rating;
import service.ServiceUser;
import user.Customer;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class StoreController implements Initializable {

    @FXML
    private Label userName, gameName, gamePrice, gameDescription, labelPublisher, labelPlatform, labelCategory, labelCartItems, labelAvailable;

    @FXML
    private ImageView imageGame;

    @FXML
    private TextField textSearch;

    @FXML
    private ScrollPane scrollGames, scrollCart;

    @FXML
    private Button buttonCart, buttonSearch;

    @FXML
    private Rating userRating;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private VBox gameDisplay, cartDisplay;

    private final GridPane gridGames = new GridPane();
    private final GridPane gridCart = new GridPane();
    private final GameDAO dao = new GameDAO();
    private List<Game> games;
    private Game displayedGame;
    private GameListener listener;
    private Timeline timer;
    private int platformIndex, categoryIndex;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        listener = this::setGame;
        search();
        scrollGames.setContent(gridGames);

        userName.setText("Hi, " + ((Customer) Main.user).getFirstName());

        setGame(games.get(0));

        labelCartItems.setText(String.valueOf(((Customer) Main.user).getShoppingCart().getCart().size()));
    }

    @FXML
    private void search() {
        try {
            games = dao.getAllGames();
        } catch (SQLException e) {
            games = null;
        }

        searchUpdate();
    }

    @FXML
    private void searchUpdate() {
        String name = textSearch.getText();
        if (name.equals(""))
            updateGrid(games);
        else
            updateGrid(games.stream().filter(g -> g.getName().toLowerCase().contains(name.toLowerCase())).collect(Collectors.toList()));
    }

    @FXML
    public void interactWithCart(MouseEvent event) {
        boolean contains;

        if (((Customer) Main.user).getShoppingCart().getCart().containsKey(displayedGame)) {
            if (!ServiceUser.removeFromCart((Customer) Main.user, displayedGame, 1))
                return;

            contains = false;
            buttonCart.setText("ADD TO CART");
        } else {
            if (!ServiceUser.addToCart((Customer) Main.user, displayedGame, 1))
                return;

            contains = true;
            buttonCart.setText("REMOVE FROM CART");
        }

        labelCartItems.setText(String.valueOf(((Customer) Main.user).getShoppingCart().getCart().size()));

        Parent root;
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/view/popup.fxml")));

        try {
            root = loader.load();
        } catch (Exception e) {
            return;
        }

        PopUpController controller = loader.getController();
        controller.setMode(contains);

        stage.setScene(new Scene(root, Color.TRANSPARENT));
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(((Node) event.getSource()).getScene().getWindow());

        anchorPane.setDisable(true);
        stage.show();
        Main.centerStage(stage);
        stage.hide();
        stage.showAndWait();
        anchorPane.setDisable(false);

    }

    @FXML
    public void clickCart() {
        int row = 0;

        gameDisplay.setVisible(false);
        cartDisplay.setVisible(true);

        gridCart.getChildren().clear();

        for (Game game : ((Customer)Main.user).getShoppingCart().getCart().keySet()) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/view/cartItem.fxml"));
                AnchorPane anchorPane = fxmlLoader.load();

                CartController controller = fxmlLoader.getController();
                controller.setData(game);

                gridCart.add(anchorPane, 0, row++);

                GridPane.setMargin(anchorPane, new Insets(5, 5, 5, 6));

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        scrollCart.setContent(gridCart);

    }

    @FXML
    public void close() {
        System.exit(0);
    }

    private void updateGrid(List<Game> list) {
        int column = 0, row = 0;
        gridGames.getChildren().clear();

        for (Game game : Objects.requireNonNull(list)) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/view/gameItem.fxml"));
                AnchorPane anchorPane = fxmlLoader.load();

                ItemController itemController = fxmlLoader.getController();
                itemController.setData(game, listener);

                if (column == 3) {
                    column = 0;
                    row++;
                }

                gridGames.add(anchorPane, column++, row);

                GridPane.setMargin(anchorPane, new Insets(5, 5, 5, 5));

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void setGame(Game game) {
        displayedGame = game;

        platformIndex = 0;
        categoryIndex = 0;

        gameName.setText(game.getName());
        gamePrice.setText(game.getPrice() + " €");
        imageGame.setImage(new Image("file:src/main/resources/game_covers/" + game.getImageName()));
        gameDescription.setText(game.getDescription());
        labelPublisher.setText(game.getPublisher());
        userRating.setRating(game.getRating().getStars());
        labelPlatform.setText(String.valueOf(game.getPlatform().get(platformIndex)));
        labelCategory.setText(String.valueOf(game.getCategory().get(categoryIndex)));
        labelAvailable.setText(game.getStatus() ? "Available" : "Unavailable");

        if (((Customer) Main.user).getShoppingCart().getCart().containsKey(displayedGame)) {
            buttonCart.setText("REMOVE FROM CART");
        } else {
            buttonCart.setText("ADD TO CART");
        }

        if (timer != null)
            timer.stop();

        if (game.getPlatform().size() > 1 || game.getCategory().size() > 1) {
            timer = new Timeline(
                    new KeyFrame(Duration.seconds(4),
                            event -> {
                                if (game.getPlatform().size() == platformIndex + 1)
                                    platformIndex = 0;
                                else
                                    platformIndex++;

                                if (game.getCategory().size() == categoryIndex + 1)
                                    categoryIndex = 0;
                                else
                                    categoryIndex++;

                                labelPlatform.setText(String.valueOf(game.getPlatform().get(platformIndex)));
                                labelCategory.setText(String.valueOf(game.getCategory().get(categoryIndex)));

                            }));

            timer.setCycleCount(Timeline.INDEFINITE);
            timer.play();
        }
    }

}
