package gui;

import dao.GameDAO;
import game.Category;
import game.Game;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import order.Order;
import org.controlsfx.control.Rating;
import service.ServiceGame;
import service.ServiceUser;
import user.Customer;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.MonetaryAmount;
import javax.money.convert.*;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.*;

import java.util.stream.Collectors;

/**
 *
 */
public class StoreController implements Initializable {

    @FXML
    private Label userName, gameName, gamePrice, gameDescription, labelPublisher,
            labelPlatform, labelCategory, labelCartItems, labelAvailable, labelTotalPrice,
            labelAvailability, labelDiscount, labelNumberOrders, labelMessage, labelMessage1;

    @FXML
    private TextField textEmail, textFirst, textLast, textPhone;

    @FXML
    private PasswordField textPassword, textPasswordConfirm;

    @FXML
    private ImageView imageGame;

    @FXML
    private TextField textSearch;

    @FXML
    private ScrollPane scrollGames, scrollCart;

    @FXML
    private Button buttonCart, buttonOrder;

    @FXML
    private Rating userRating;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private VBox gameDisplay, cartDisplay, accountDisplay;

    @FXML
    private ComboBox<String> comboSort, comboFilter, comboFilterCase;

    private final GridPane gridGames = new GridPane();
    private final GridPane gridCart = new GridPane();
    private final GameDAO dao = new GameDAO();
    private List<Game> games, displayedGames;
    private Game displayedGame;
    private GameListener listener;
    private Timeline timer;
    private int platformIndex, categoryIndex;

    /**
     *
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        listener = this::setGame;
        userName.setText("Hi, " + ((Customer) Main.user).getFirstName());

        search();

        setGame(games.get(0));
        scrollGames.setContent(gridGames);

        labelCartItems.setText(String.valueOf(((Customer) Main.user).getShoppingCart().getCart().size()));

        initializeCombos();

        if (((Customer) Main.user).getNumberOfOrders() % 5 != 4) {
            labelDiscount.setText("No discount is applicable");
        } else {
            labelDiscount.setText("Discount is applied");
        }
    }

    /**
     *
     */
    @FXML
    public void logout(MouseEvent event) {
        Parent root;
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/view/popup.fxml")));

        try {
            root = loader.load();
        } catch (Exception e) {
            return;
        }

        PopUpController controller = loader.getController();
        controller.setMode(4, null);
        PopUpController.setUp(root, stage, anchorPane, event);


    }

    /**
     *
     */
    @FXML
    private void search() {
        try {
            games = dao.getAllGames();
        } catch (SQLException e) {
            games = null;
        }

        searchUpdate();
    }

    /**
     *
     */
    @FXML
    private void searchUpdate() {
        List<Game> temp = null;
        String name = textSearch.getText();
        displayedGames = games.stream().filter(g -> g.getName().toLowerCase().contains(name.toLowerCase())).collect(Collectors.toList());

        if(comboFilterCase.getValue() != null){
            switch (comboFilter.getValue()) {
                case "Category" -> temp = ServiceGame.filterGamesCategory(comboFilterCase.getValue());
                case "Platform" -> temp = ServiceGame.filterGamesPlatform(comboFilterCase.getValue());
                case "Publisher" -> temp = ServiceGame.filterGamesPublisher(comboFilterCase.getValue());
                case "Rating" -> temp = ServiceGame.filterGamesRating(Integer.parseInt(comboFilterCase.getValue()));
            }

            displayedGames = Objects.requireNonNull(temp).stream()
                    .distinct()
                    .filter(displayedGames::contains)
                    .collect(Collectors.toList());
        }

        if(comboSort.getValue() != null && !comboSort.getValue().equals("None")) {
            switch (comboSort.getValue()) {
                case "Alphabetical order" -> temp = ServiceGame.sortGamesName(true);
                case "Reverse alphabetical order" -> temp = ServiceGame.sortGamesName(false);
                case "Ascending price" -> temp = ServiceGame.sortGamesPrice(true);
                case "Descending price" -> temp = ServiceGame.sortGamesPrice(false);
                case "Ascending rating" -> temp = ServiceGame.sortGamesRating(true);
                case "Descending rating" -> temp = ServiceGame.sortGamesRating(false);
            }

            displayedGames = Objects.requireNonNull(temp).stream()
                    .distinct()
                    .filter(displayedGames::contains)
                    .collect(Collectors.toList());
        }

        updateGrid(displayedGames);
    }

    /**
     *
     */
    @FXML
    private void interactWithCart(MouseEvent event) {
        int contains;

        if (((Customer) Main.user).getShoppingCart().getCart().containsKey(displayedGame)) {
            if (!ServiceUser.removeFromCart((Customer) Main.user, displayedGame, ((Customer) Main.user).getShoppingCart().getCart().get(displayedGame)))
                return;

            contains = 1;
            buttonCart.setText("ADD TO CART");
        } else {
            if (!ServiceUser.addToCart((Customer) Main.user, displayedGame, 1))
                return;

            contains = 0;
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
        controller.setMode(contains, null);
        PopUpController.setUp(root, stage, anchorPane, event);
    }

    /**
     *
     */
    @FXML
    void clickCart() {
        int row = 0;

        gameDisplay.setVisible(false);
        accountDisplay.setVisible(false);
        cartDisplay.setVisible(true);

        gridCart.getChildren().clear();

        labelTotalPrice.setText(((Customer) Main.user).getShoppingCart().getTotalPrice() + " €");
        labelAvailability.setText("All items are available");
        buttonOrder.setDisable(false);

        for (Game game : ((Customer) Main.user).getShoppingCart().getCart().keySet()) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/view/cartItem.fxml"));
                AnchorPane anchorPane = fxmlLoader.load();

                CartController controller = fxmlLoader.getController();
                controller.setData(game, this);

                gridCart.add(anchorPane, 0, row++);

                GridPane.setMargin(anchorPane, new Insets(5, 0, 5, 3));

                if (!game.getStatus()) {
                    labelAvailability.setText("Some items are not available");
                    buttonOrder.setDisable(true);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (((Customer) Main.user).getShoppingCart().getCart().size() == 0) {
            labelAvailability.setText("Cart is empty");
            buttonOrder.setDisable(true);
        }

        scrollCart.setContent(gridCart);
    }

    /**
     *
     */
    @FXML
    private void clickStore() {
        cartDisplay.setVisible(false);
        accountDisplay.setVisible(false);
        gameDisplay.setVisible(true);
    }

    /**
     *
     */
    @FXML
    private void close() {
        System.exit(0);
    }

    /**
     *
     */
    @FXML
    private void order(MouseEvent event) {
        Order newOrder = ServiceUser.placeOrder((Customer) Main.user);

        Parent root;
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/view/popup.fxml")));

        try {
            root = loader.load();
        } catch (Exception e) {
            return;
        }

        PopUpController controller = loader.getController();

        if (newOrder != null) {
            controller.setMode(2, newOrder);
        } else {
            controller.setMode(3, null);
        }

        PopUpController.setUp(root, stage, anchorPane, event);

        clickCart();
        updateLabels();
    }

    /**
     *
     */
    @FXML
    public void changeCurrency(MouseEvent event) {
        if (((Label) event.getSource()).getText().endsWith("€")) {
            CurrencyUnit euro = Monetary.getCurrency("EUR");

            MonetaryAmount money;

            if (gameDisplay.isVisible())
                money = Monetary.getDefaultAmountFactory().setCurrency(euro).setNumber(displayedGame.getPrice()).create();
            else
                money = Monetary.getDefaultAmountFactory().setCurrency(euro).setNumber(((Customer) Main.user).getShoppingCart().getTotalPrice()).create();

            ConversionQuery conversionQuery = ConversionQueryBuilder.of().setTermCurrency("RON").
                    set(Date.class, new Date()).build();


            final CurrencyConversion ronConversion = MonetaryConversions.getExchangeRateProvider().getCurrencyConversion(conversionQuery);
            final MonetaryAmount inRon = money.with(ronConversion);

            ((Label) event.getSource()).setText(inRon.toString());
        } else {
            if (gameDisplay.isVisible())
                ((Label) event.getSource()).setText(displayedGame.getPrice() + " €");
            else
                ((Label) event.getSource()).setText(((Customer)Main.user).getShoppingCart().getTotalPrice() + " €");
        }
    }

    /**
     *
     */
    @FXML
    public void rating() {
        ServiceUser.setCustomerRating(((Customer) Main.user),
                displayedGame, (int) userRating.getRating());
    }

    /**
     *
     */
    @FXML
    public void settings() {
        Customer customer = (Customer)Main.user;

        cartDisplay.setVisible(false);
        gameDisplay.setVisible(false);
        accountDisplay.setVisible(true);

        textFirst.setPromptText(customer.getFirstName());
        textLast.setPromptText(customer.getLastName());
        textEmail.setPromptText(customer.getEmail());
        textPhone.setPromptText(customer.getPhoneNumber());

        textFirst.setText(null);
        textLast.setText(null);
        textEmail.setText(null);
        textPhone.setText(null);

        labelMessage.setVisible(false);
        labelMessage1.setVisible(false);

        labelNumberOrders.setText("You have " + customer.getNumberOfOrders() + " order(s) on this account.");
    }

    /**
     *
     */
    @FXML
    public void discardChanges() {
        textFirst.setText(null);
        textLast.setText(null);
        textEmail.setText(null);
        textPhone.setText(null);
        labelMessage1.setVisible(false);
    }

    /**
     *
     * @param event e
     */
    @FXML
    public void saveChanges(MouseEvent event) {
        Customer user = (Customer)Main.user;
        Customer customer = new Customer(user.getId(), user.getEmail(),
                user.getPasswordHash(), user.getFirstName(), user.getLastName(),
                user.getPhoneNumber(), user.getOrders(), user.getShoppingCart());

        String email, firstName, lastName, phoneNumber;
        email = textEmail.getText();
        firstName = textFirst.getText();
        lastName = textLast.getText();
        phoneNumber = textPhone.getText();

        if(firstName != null && !firstName.equals("")){
            if (firstName.matches("^[a-zA-Z]{2,}$")) {
                firstName = firstName.substring(0, 1).toUpperCase() + firstName.substring(1).toLowerCase();
                customer.setFirstName(firstName);
            } else {
                labelMessage1.setText("First name is invalid.");
                labelMessage1.setVisible(true);
                return;
            }
        }

        if(lastName != null && !lastName.equals("")){
            if (lastName.matches("^[a-zA-Z]{2,}$")) {
                lastName = lastName.substring(0, 1).toUpperCase() + lastName.substring(1).toLowerCase();
                customer.setLastName(lastName);
            } else {
                labelMessage1.setText("Last name is invalid.");
                labelMessage1.setVisible(true);
                return;
            }
        }

        if(email != null && !email.equals("")){
            if (email.matches("^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")) {
                email = email.toLowerCase();
                customer.setEmail(email);
            } else {
                labelMessage1.setText("Email address is invalid.");
                labelMessage1.setVisible(true);
                return;
            }
        }

        if(phoneNumber != null && !phoneNumber.equals("")){
            if (!phoneNumber.matches("^[0-9]{5,}$")) {
                labelMessage1.setText("Phone number is invalid.");
                labelMessage1.setVisible(true);
                return;
            } else {
                customer.setPhoneNumber(phoneNumber);
            }
        }

        if (!ServiceUser.changeCustomerData(customer)) {
            labelMessage1.setText("Unexpected error.");
            labelMessage1.setVisible(true);
        } else {
            Parent root;
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/view/popup.fxml")));

            try {
                root = loader.load();
            } catch (Exception e) {
                labelMessage1.setText("Unexpected error.");
                labelMessage1.setVisible(true);
                return;
            }

            Main.user = customer;
            settings();

            PopUpController controller = loader.getController();
            controller.setMode(6, null);
            PopUpController.setUp(root, stage, anchorPane, event);
        }
    }

    /**
     *
     * @param event m
     */
    @FXML
    public void resetPassword(MouseEvent event) {
        String oldPassword = textPassword.getText();
        String newPassword = textPasswordConfirm.getText();

        if (oldPassword == null || newPassword == null || oldPassword.equals("") || newPassword.equals("")) {
            labelMessage.setText("Please fill both fields.");
            labelMessage.setVisible(true);
            return;
        }

        if (oldPassword.equals(newPassword)) {
            labelMessage.setText("The new password must be different from the old one.");
            labelMessage.setVisible(true);
            return;
        }

        if (!newPassword.matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$")) {
            labelMessage.setText("Password must contain minimum 8 characters,\n       at least one letter and one number.");
            labelMessage.setVisible(true);
            return;
        }

        if (!ServiceUser.changePassword(Main.user, oldPassword, newPassword)) {
            labelMessage.setText("The old password is wrong!");
            labelMessage.setVisible(true);
        } else {
            Parent root;
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/view/popup.fxml")));

            try {
                root = loader.load();
            } catch (Exception e) {
                labelMessage.setText("Unexpected error.");
                labelMessage.setVisible(true);
                return;
            }

            settings();

            PopUpController controller = loader.getController();
            controller.setMode(5, null);
            PopUpController.setUp(root, stage, anchorPane, event);
        }
    }

    /**
     *
     */
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

                GridPane.setMargin(anchorPane, new Insets(5, 7, 5, 7));

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     *
     */
    private void setGame(Game game) {
        displayedGame = game;

        clickStore();

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
        userRating.setRating(ServiceUser.getCustomerRating(((Customer) Main.user), game));

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

    /**
     *
     */
    void updateLabels() {
        labelCartItems.setText(String.valueOf(((Customer) Main.user).getShoppingCart().getCart().size()));
        labelTotalPrice.setText(((Customer) Main.user).getShoppingCart().getTotalPrice() + " €");

        buttonOrder.setDisable(false);
        if (((Customer) Main.user).getShoppingCart().getCart().size() == 0)
            labelAvailability.setText("Cart is empty");
        else
            labelAvailability.setText("All items are available");

        for (Game game : ((Customer) Main.user).getShoppingCart().getCart().keySet()) {
            if (!game.getStatus()) {
                labelAvailability.setText("Some items are not available");
                buttonOrder.setDisable(true);
                break;
            }
        }
    }

    /**
     *
     */
    private void initializeCombos() {
        comboSort.setItems(FXCollections.observableArrayList("None",
                "Alphabetical order",
                "Reverse alphabetical order",
                "Ascending price",
                "Descending price",
                "Ascending rating",
                "Descending rating"));

        comboSort.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (oldVal == null || oldVal.compareTo(newVal) != 0) {
                List<Game> temp = null;

                switch (newVal) {
                    case "None" -> searchUpdate();
                    case "Alphabetical order" -> temp = ServiceGame.sortGamesName(true);
                    case "Reverse alphabetical order" -> temp = ServiceGame.sortGamesName(false);
                    case "Ascending price" -> temp = ServiceGame.sortGamesPrice(true);
                    case "Descending price" -> temp = ServiceGame.sortGamesPrice(false);
                    case "Ascending rating" -> temp = ServiceGame.sortGamesRating(true);
                    case "Descending rating" -> temp = ServiceGame.sortGamesRating(false);
                }

                if (temp != null) {
                    displayedGames = temp.stream()
                            .distinct()
                            .filter(displayedGames::contains)
                            .collect(Collectors.toList());
                }

                updateGrid(displayedGames);
            }
        });

        comboFilter.setItems(FXCollections.observableArrayList("None",
                "Category",
                "Platform",
                "Publisher",
                "Rating"));

        comboFilter.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (oldVal == null || oldVal.compareTo(newVal) != 0) {
                switch (newVal) {
                    case "None" -> {
                        comboFilterCase.setPromptText("");
                        comboFilterCase.setItems(null);
                        searchUpdate();
                    }

                    case "Category" -> {
                        comboFilterCase.setPromptText("Select category");
                        comboFilterCase.setItems(FXCollections.observableList(Arrays.stream(Category.values()).
                                map(Category::toString).collect(Collectors.toList())));
                    }

                    case "Platform" -> {
                        comboFilterCase.setPromptText("Select platform");
                        comboFilterCase.setItems(FXCollections.observableList(Objects.requireNonNull(ServiceGame.getPlatforms())));
                    }

                    case "Publisher" -> {
                        comboFilterCase.setPromptText("Select publisher");
                        comboFilterCase.setItems(FXCollections.observableList(Objects.requireNonNull(ServiceGame.getPublishers())));

                    }

                    case "Rating" -> {
                        comboFilterCase.setPromptText("Select rating");
                        comboFilterCase.setItems(FXCollections.observableArrayList("1", "2", "3", "4", "5"));
                    }
                }
            }
        });

        comboFilterCase.valueProperty().addListener((obs, oldVal, newVal) -> {
            if(newVal == null)
                return;
            searchUpdate();
        });
    }
}
