package service;

import dao.GameDAO;
import email.EmailManager;
import exceptions.InvalidCodeException;
import exceptions.UnavailableGameException;
import game.Game;
import gui.ConfirmationController;
import gui.LoginController;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import order.ShoppingCart;
import user.Customer;
import order.Order;
import user.User;
import dao.UserDAO;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * ServiceUser is a utility class that contains the
 * possible actions performed by a user.
 *
 * @author      Marius Bubui
 * @version     1.0
 */
public class ServiceUser {
    /**
     * A data acces object for the user.
     */
    private static final UserDAO userDAO = new UserDAO();

    /**
     * A data acces object for the game.
     */
    private static final GameDAO gameDAO = new GameDAO();

    /**
     * Methode that provides the login interface
     * at the service level.
     *
     * @param email    user's email
     * @param password user's password
     * @return a User object if the login is successful or null
     */
    public static User login(String email, String password) {
        try {
            return userDAO.login(email, password);
        } catch (SQLException e) {
            return null;
        }
    }

    /**
     * Methode that provides the logout interface
     * at the service level.
     * Sets all the user's attributes to null.
     *
     * @param user object
     */
    public static void logout(User user) {
        user.setEmail(null);
        user.setPasswordHash(null);

        if (user instanceof Customer) {
            ((Customer) user).setFirstName(null);
            ((Customer) user).setLastName(null);
            ((Customer) user).setPhoneNumber(null);
            ((Customer) user).setOrders(null);
            ((Customer) user).setShoppingCart(null);
        }
    }

    /**
     * Methode that provides the registration
     * interface at the service level.
     *
     * @param email                  user's email
     * @param firstName              user's first name
     * @param lastName               user's last name
     * @param password               user's password
     * @param phoneNumber            user's phone number
     * @param root                   application's root node
     * @param confirmationController application's confirmation stage controller
     * @param loginController        application's login stage controller
     * @param event                  the event that triggered the action
     * @return a User object if the registration is successful or null
     * @throws InvalidCodeException if the validation code is wrong
     */
    public static User register(String email, String firstName, String lastName, String password, String phoneNumber, Parent root,
                                ConfirmationController confirmationController, LoginController loginController, MouseEvent event) throws InvalidCodeException {
        try {
            String code = EmailManager.generateKey();
            confirmationController.setCode(code, loginController);

            new Thread(() -> {
                try {
                    MimeMessage message = EmailManager.getConfirmationMessage(email, code);
                    EmailManager.sendEmail(message);
                } catch (MessagingException | IOException e) {
                    e.printStackTrace();
                }
            }).start();

            loginController.confirmation(root, event);

            if (loginController.getConfirmation())
                return userDAO.register(email, password, firstName, lastName, phoneNumber);
            else
                throw new InvalidCodeException();
        } catch (SQLException e) {
            return null;
        }
    }

    /**
     * Methode that provides the facility to
     * add a game to the cart.
     *
     * @param customer the logged customer
     * @param game     the game to be added to the cart
     * @param quantity the number of games
     * @return true or false representing the status of the action
     */
    public static boolean addToCart(Customer customer, Game game, int quantity) {
        try {
            userDAO.addGameCart(customer, game, quantity);
            customer.getShoppingCart().addGame(game, quantity);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Methode that provides the facility to
     * remove a game to the cart.
     *
     * @param customer the logged customer
     * @param game     the game to be removed from the cart
     * @param quantity the number of games
     * @return true or false representing the status of the action
     */
    public static boolean removeFromCart(Customer customer, Game game, int quantity) {
        try {
            userDAO.removeGameCart(customer, game, quantity);
            customer.getShoppingCart().removeGame(game, quantity);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Methode that provides the ordering
     * interface at the service level.
     *
     * @param customer the logged customer
     * @return the new order or null
     */
    public static Order placeOrder(Customer customer) {
        try {
            Order order = userDAO.placeOrder(customer);

            new Thread(() -> {
                try {
                    MimeMessage message = EmailManager.getInvoiceMessage(customer, order);
                    EmailManager.sendEmail(message);
                } catch (MessagingException | IOException e) {
                    e.printStackTrace();
                }
            }).start();

            customer.setShoppingCart(new ShoppingCart());
            customer.getOrders().add(order);
            customer.setNumberOfOrders(customer.getNumberOfOrders() + 1);

            return order;
        } catch (UnavailableGameException | SQLException e) {
            Map<Game, Integer> cart = new HashMap<>();

            try {
                for (Game game : customer.getShoppingCart().getCart().keySet())
                    cart.put(gameDAO.getGame(game.getId()), customer.getShoppingCart().getCart().get(game));
                customer.getShoppingCart().setCart(cart);
            } catch (SQLException ignore) {
            }

            return null;
        }
    }

    /**
     * @param customer c
     * @param game     g
     * @param rating   r
     */
    public static void setCustomerRating(Customer customer, Game game, int rating) {
        try {
            userDAO.setCustomerRating(customer, game, rating);
        } catch (SQLException ignored) {
        }
    }

    /**
     *
     * @param customer c
     * @param game g
     * @return i
     */
    public static int getCustomerRating(Customer customer, Game game) {
        try {
            return userDAO.getCustomerRating(customer, game);
        } catch (SQLException ignored) {
            return 0;
        }
    }

    /**
     *
     * @param user u
     * @param oldPassword  o
     * @param newPassword n
     * @return r
     */
    public static boolean changePassword(User user, String oldPassword, String newPassword){
        try {
            return userDAO.changePassword(user, oldPassword, newPassword);
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     *
     * @param customer c
     * @return r
     */
    public static boolean changeCustomerData(Customer customer){
        try {
            return userDAO.changeCustomerData(customer);
        } catch (SQLException e) {
            return false;
        }
    }
}