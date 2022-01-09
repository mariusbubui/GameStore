package dao;

import exceptions.UnavailableGameException;
import game.Game;
import order.Order;
import user.Customer;
import user.User;

import java.sql.SQLException;

public interface IUserDAO {
    User checkLogin(String email, String password) throws SQLException;
    User checkRegistration(String email, String password, String firstName, String lastName, String phoneNumber) throws SQLException;
    Order checkOrder(Customer customer) throws SQLException, UnavailableGameException;
    void addGameCart(Customer customer, Game game, int quantity) throws SQLException;
    void removeFromCart(Customer customer, Game game, int quantity) throws SQLException;
}
