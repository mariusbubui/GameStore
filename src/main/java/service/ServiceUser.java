package service;

import exceptions.UnavailableGameException;
import game.Game;
import order.ShoppingCart;
import user.Customer;
import order.Order;
import user.User;
import dao.UserDAO;

import java.sql.SQLException;
import java.util.Map;

public class ServiceUser {
    private static final UserDAO userDAO = new UserDAO();

    public static User login(String email, String password) {
        try {
            return userDAO.checkLogin(email, password);
        } catch (SQLException e) {
            return null;
        }
    }

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

    public static User register(String email, String firstName, String lastName, String password, String phoneNumber) {
        try {
            return userDAO.checkRegistration(email, password, firstName, lastName, phoneNumber);
        } catch (SQLException e) {
            return null;
        }
    }

    public static boolean addToCart(Customer customer, Game game, int quantity) {
        try {
            userDAO.addGameCart(customer, game, quantity);
            customer.getShoppingCart().addGame(game, quantity);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public static boolean removeFromCart(Customer customer, Game game, int quantity) {
        try {
            userDAO.removeFromCart(customer, game, quantity);
            customer.getShoppingCart().removeGame(game, quantity);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public static void removeFromCart(Customer customer, Game game, boolean all) {
        Map<Game, Integer> cart = customer.getShoppingCart().getCart();
        if (all || cart.get(game) == 1)
            cart.remove(game);
        else
            cart.put(game, cart.get(game) - 1);
    }

    public static Order placeOrder(Customer customer) throws UnavailableGameException {
        try {
            Order order = userDAO.checkOrder(customer);

            customer.setShoppingCart(new ShoppingCart());
            customer.getOrders().add(order);
            customer.setNumberOfOrders(customer.getNumberOfOrders() + 1);

            return order;
        } catch (SQLException e) {
            return null;
        }
    }

}