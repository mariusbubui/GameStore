package dao;

import exceptions.UnavailableGameException;
import game.Game;
import order.Order;
import user.Customer;
import user.User;

import java.sql.SQLException;

/**
 *
 */
public interface IUserDAO {
    /**
     *
     */
    User login(String email, String password) throws SQLException;

    /**
     *
     */
    User register(String email, String password, String firstName, String lastName, String phoneNumber) throws SQLException;

    /**
     *
     */
    Order placeOrder(Customer customer) throws SQLException, UnavailableGameException;

    /**
     *
     */
    void addGameCart(Customer customer, Game game, int quantity) throws SQLException;

    /**
     *
     */
    void removeGameCart(Customer customer, Game game, int quantity) throws SQLException;

    /**
     *
     */
    void emptyCart(Customer customer) throws SQLException;

    /**
     *
     * @return the
     */
    int getCustomerRating(Customer customer, Game game) throws SQLException;

    /**
     *
     * @param customer c
     * @param game g
     * @param rating r
     * @throws SQLException s
     */
    void setCustomerRating(Customer customer, Game game, int rating) throws SQLException;

    /**
     *
     * @param user u
     * @param oldPassword o
     * @param newPassword n
     * @return r
     * @throws SQLException s
     */
    boolean changePassword(User user, String oldPassword, String newPassword) throws SQLException;

    /**
     *
     * @param customer c
     * @return r
     * @throws SQLException e
     */
    boolean changeCustomerData(Customer customer) throws SQLException;
}
