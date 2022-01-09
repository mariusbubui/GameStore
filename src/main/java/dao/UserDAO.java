package dao;

import at.favre.lib.crypto.bcrypt.BCrypt;
import exceptions.UnavailableGameException;
import game.*;
import order.Order;
import order.ShoppingCart;
import user.Admin;
import user.Customer;
import user.User;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.*;

public class UserDAO implements IUserDAO {
    @Override
    public User checkLogin(String email, String password) throws SQLException {

        Connection conn = ConnectionManager.getConnection();
        Statement st = Objects.requireNonNull(conn).createStatement();

        String query = "SELECT * FROM user_view WHERE email = '" + email + "'";
        ResultSet rs = st.executeQuery(query);

        if (rs.next() && BCrypt.verifyer().verify(password.toCharArray(), rs.getString("password")).verified) {
            if (rs.getBoolean("admin")) {

                ConnectionManager.closeConnection();

                return new Admin(rs.getInt("user_id"),
                        rs.getString("email"),
                        rs.getString("password"));

            } else {
                Customer customer = new Customer(
                        rs.getInt("user_id"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("phone_number"),
                        null,
                        null);

                query = "SELECT order_id, order_date, game_id, quantity FROM \"order\" " +
                        "JOIN order_game ON \"order\".id = order_game.order_id " +
                        "WHERE user_id = " + customer.getId();
                rs = st.executeQuery(query);

                List<Order> orders = new ArrayList<>();

                while (rs.next()) {
                    boolean ok = false;
                    for (Order o : orders) {
                        if (o.getId() == rs.getInt("order_id")) {
                            Game game = new GameDAO().getGame(rs.getInt("game_id"));
                            o.getOrder().put(game, rs.getInt("quantity"));
                            o.setTotalPrice(o.getTotalPrice() + game.getPrice() * rs.getInt("quantity"));
                            ok = true;
                            break;
                        }
                    }

                    if (!ok) {
                        Game game = new GameDAO().getGame(rs.getInt("game_id"));
                        ResultSet finalRs = rs;
                        orders.add(new Order(rs.getInt("order_id"),
                                new HashMap<>() {{
                                    put(game, finalRs.getInt("quantity"));
                                }},
                                rs.getObject("order_date", LocalDate.class)));
                    }
                }

                customer.setOrders(orders);

                query = "SELECT game_id, quantity FROM cart WHERE user_id = " + customer.getId();
                rs = st.executeQuery(query);
                ConnectionManager.closeConnection();

                Map<Game, Integer> cart = new HashMap<>();
                while (rs.next()) {
                    cart.put(new GameDAO().getGame(rs.getInt("game_id")), rs.getInt("quantity"));
                }

                customer.setShoppingCart(new ShoppingCart(cart));

                return customer;
            }
        }

        ConnectionManager.closeConnection();
        return null;
    }

    @Override
    public User checkRegistration(String email, String password, String firstName, String lastName, String phoneNumber) throws SQLException {

        Connection conn = ConnectionManager.getConnection();
        Statement st = Objects.requireNonNull(conn).createStatement();

        String query = "SELECT id FROM \"user\" WHERE email = '" + email + "'";
        ResultSet rs = st.executeQuery(query);

        if (!rs.next()) {
            query = "INSERT INTO \"user\"(email, password, admin) " +
                    "VALUES('" + email + "', '" + BCrypt.withDefaults().hashToString(12, password.toCharArray()) + "', false)";

            st.executeUpdate(query);

            query = "SELECT id FROM \"user\" WHERE email='" + email + "'";

            rs = st.executeQuery(query);
            rs.next();

            query = "INSERT INTO customer(user_id, first_name, last_name, phone_number) " +
                    "VALUES(" + rs.getInt("id") + ", '" + firstName + "', '" + lastName + "', '" + phoneNumber + "');";

            st.executeUpdate(query);

            ConnectionManager.closeConnection();

            return checkLogin(email, password);
        } else {
            ConnectionManager.closeConnection();
            return null;
        }
    }

    @Override
    public Order checkOrder(Customer customer) throws SQLException, UnavailableGameException {
        Connection conn = ConnectionManager.getConnection();
        Statement st = Objects.requireNonNull(conn).createStatement();

        String query;
        ResultSet rs;

        for (Game game : customer.getShoppingCart().getCart().keySet()) {
            query = "SELECT status FROM game WHERE id = '" + game.getId() + "';";
            rs = st.executeQuery(query);
            rs.next();
            if (!rs.getBoolean("status"))
                throw new UnavailableGameException(game);
        }

        query = "INSERT INTO \"order\"(user_id) VALUES (" + customer.getId() + ");";
        st.executeUpdate(query);

        query = "SELECT id, order_date FROM \"order\" LEFT JOIN order_game og on \"order\".id = og.order_id WHERE order_id is NULL;";
        rs = st.executeQuery(query);
        rs.next();

        StringBuilder orderString = new StringBuilder();
        int orderId = rs.getInt("id");
        LocalDate date = rs.getObject("order_date", LocalDate.class);

        for (Map.Entry<Game, Integer> set : customer.getShoppingCart().getCart().entrySet()) {
            orderString.append('(').append(orderId)
                    .append(", ").append(set.getKey().getId())
                    .append(", ").append(set.getValue())
                    .append("),");
        }

        orderString.setLength(Math.max(orderString.length() - 1, 0));
        orderString.append(';');

        query = "INSERT INTO order_game VALUES " + orderString;
        st.executeUpdate(query);

        ConnectionManager.closeConnection();

        return new Order(orderId, customer.getShoppingCart().getCart(), date);
    }

    @Override
    public void addGameCart(Customer customer, Game game, int quantity) throws SQLException {
        Connection conn = ConnectionManager.getConnection();
        Statement st = Objects.requireNonNull(conn).createStatement();
        String query;

        if (customer.getShoppingCart().getCart().containsKey(game)) {
            query = "UPDATE cart SET quantity = '" + (customer.getShoppingCart().getCart().get(game) + quantity) +
                    "' WHERE user_id = '" + customer.getId() + "' AND game_id = '" + game.getId() + "'";
        } else {
            query = "INSERT INTO cart(user_id, game_id, quantity) VALUES(" + customer.getId() + ", " + game.getId() + ", " + quantity +")";
        }

        st.executeUpdate(query);
        ConnectionManager.closeConnection();
    }

    @Override
    public void removeFromCart(Customer customer, Game game, int quantity) throws SQLException {
        Connection conn = ConnectionManager.getConnection();
        Statement st = Objects.requireNonNull(conn).createStatement();
        String query;

        if (customer.getShoppingCart().getCart().get(game) <= quantity) {
            query = "DELETE FROM cart WHERE user_id = '" + customer.getId() + "' AND game_id = '" + game.getId() + "'";
        } else {
            query = "UPDATE cart SET quantity = '" + (customer.getShoppingCart().getCart().get(game) - quantity) +
                    "' WHERE user_id = '" + customer.getId() + "' AND game_id = '" + game.getId() + "'";
        }

        st.executeUpdate(query);
        ConnectionManager.closeConnection();
    }
}
