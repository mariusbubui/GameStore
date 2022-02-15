package dao;

import at.favre.lib.crypto.bcrypt.BCrypt;
import exceptions.UnavailableGameException;
import game.*;
import order.Order;
import order.ShoppingCart;
import user.Admin;
import user.Customer;
import user.User;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;

public class UserDAO implements IUserDAO {
    @Override
    public User login(String email, String password) throws SQLException {
        User user = null;

        try (Connection conn = ConnectionManager.getConnection()) {
            assert conn != null;

            try(PreparedStatement st = conn.prepareStatement("SELECT * FROM customer_view WHERE email = ?")) {
                st.setString(1, email);

                try (ResultSet rs = st.executeQuery()) {
                    if (rs.next() && BCrypt.verifyer().verify(password.toCharArray(), rs.getString("password")).verified) {
                        if (rs.getBoolean("admin"))
                            user = new Admin(rs.getInt("id"),
                                    rs.getString("email"),
                                    rs.getString("password"));
                        else {
                            user = new Customer(
                                    rs.getInt("id"),
                                    rs.getString("email"),
                                    rs.getString("password"),
                                    rs.getString("first_name"),
                                    rs.getString("last_name"),
                                    rs.getString("phone_number"),
                                    null,
                                    null);
                        }
                    }
                }
            }

            if(user instanceof Customer){
                List<Order> orders = new ArrayList<>();
                ShoppingCart cart = new ShoppingCart();

                String query = "SELECT order_id, order_date, game_id, quantity FROM \"order\" " +
                        "JOIN order_game ON \"order\".id = order_game.order_id WHERE user_id = ?";

                try(PreparedStatement st = conn.prepareStatement(query)){
                    st.setInt(1, user.getId());

                    try (ResultSet rs = st.executeQuery()) {
                        while (rs.next()) {
                            boolean ok = false;
                            for (Order order : orders) {
                                if (order.getId() == rs.getInt("order_id")) {
                                    Game game = new GameDAO().getGame(rs.getInt("game_id"));

                                    order.getOrder().put(game, rs.getInt("quantity"));
                                    order.setTotalPrice(order.getTotalPrice() + game.getPrice() * rs.getInt("quantity"));

                                    ok = true;
                                    break;
                                }
                            }

                            if (!ok) {
                                Game game = new GameDAO().getGame(rs.getInt("game_id"));

                                orders.add(new Order(rs.getInt("order_id"),
                                        new HashMap<>() {{
                                            put(game, rs.getInt("quantity"));
                                        }},
                                        rs.getObject("order_date", LocalDate.class)));
                            }
                        }
                    }
                }

                query = "SELECT game_id, quantity FROM cart WHERE user_id = ?";

                try(PreparedStatement st = conn.prepareStatement(query)) {
                    st.setInt(1, user.getId());

                    try (ResultSet rs = st.executeQuery()) {
                        while (rs.next()) {
                            cart.addGame(new GameDAO()
                                    .getGame(rs.getInt("game_id")), rs.getInt("quantity"));
                        }
                    }
                }

                ((Customer)user).setOrders(orders);
                ((Customer)user).setShoppingCart(cart);
            }
        }

        return user;
    }

    @Override
    public User register(String email, String password, String firstName, String lastName, String phoneNumber) throws SQLException {
        boolean exists = true;

        try (Connection conn = ConnectionManager.getConnection()) {
            assert conn != null;

            try (PreparedStatement st = conn.prepareStatement("SELECT id FROM \"user\" WHERE email = ?")) {
                st.setString(1, email);

                try (ResultSet rs = st.executeQuery()) {
                    if (!rs.next())
                        exists = false;
                }
            }

            if (!exists) {
                int id;
                String query = "INSERT INTO \"user\"(email, password, admin) " +
                        "VALUES(?, ?, ?)";

                try (PreparedStatement st = conn.prepareStatement(query)) {
                    st.setString(1, email);
                    st.setString(2, BCrypt.withDefaults().hashToString(12, password.toCharArray()));
                    st.setBoolean(3, false);
                    st.executeUpdate();
                }

                try (PreparedStatement st = conn.prepareStatement("SELECT id FROM \"user\" WHERE email = ?")) {
                    st.setString(1, email);

                    try (ResultSet rs = st.executeQuery()) {
                        rs.next();
                        id = rs.getInt("id");
                    }
                }

                query = "INSERT INTO customer(user_id, first_name, last_name, phone_number) VALUES (?, ?, ?, ?);";
                try (PreparedStatement st = conn.prepareStatement(query)) {
                    st.setInt(1, id);
                    st.setString(2, firstName);
                    st.setString(3, lastName);
                    st.setString(4, phoneNumber);
                    st.executeUpdate();
                }
            }
        }

        return login(email, password);
    }

    @Override
    public Order placeOrder(Customer customer) throws SQLException, UnavailableGameException {
        int orderId;
        LocalDate date;
        Connection conn = ConnectionManager.getConnection();
        assert conn != null;

        try {
            conn.setAutoCommit(false);

            for (Game game : customer.getShoppingCart().getCart().keySet()) {
                try (PreparedStatement st = conn.prepareStatement("SELECT status FROM game WHERE id = ?")) {
                    st.setInt(1, game.getId());

                    try (ResultSet rs = st.executeQuery()) {
                        rs.next();
                        if (!rs.getBoolean("status"))
                            throw new UnavailableGameException(game);
                    }
                }
            }

            try (PreparedStatement st = conn.prepareStatement("INSERT INTO \"order\"(user_id) VALUES (?)")) {
                st.setInt(1, customer.getId());
                st.executeUpdate();
            }

            String query = "SELECT id, order_date FROM \"order\" LEFT JOIN order_game on id = order_id WHERE order_id is NULL;";
            try (PreparedStatement st = conn.prepareStatement(query)) {
                try (ResultSet rs = st.executeQuery()) {
                    rs.next();
                    orderId = rs.getInt("id");
                    date = rs.getObject("order_date", LocalDate.class);
                }
            }

            StringBuilder orderString = new StringBuilder();
            for (Map.Entry<Game, Integer> set : customer.getShoppingCart().getCart().entrySet()) {
                orderString.append('(').append(orderId)
                        .append(", ").append(set.getKey().getId())
                        .append(", ").append(set.getValue())
                        .append("),");
            }

            orderString.setLength(Math.max(orderString.length() - 1, 0));
            orderString.append(';');

            try (Statement st = conn.createStatement()) {
                st.executeUpdate("INSERT INTO order_game VALUES " + orderString);
            }
            conn.commit();

        } catch (SQLException e) {
            conn.rollback();
            return null;
        }
        finally{
            conn.close();
        }

        emptyCart(customer);
        return new Order(orderId, customer.getShoppingCart().getCart(), date);
    }

    @Override
    public void addGameCart(Customer customer, Game game, int quantity) throws SQLException {
        try (Connection conn = ConnectionManager.getConnection()) {
            assert conn != null;

            String query;
            if (customer.getShoppingCart().getCart().containsKey(game)) {
                query = "UPDATE cart SET quantity = ? WHERE user_id = ? AND game_id = ?";
            } else {
                query = "INSERT INTO cart(user_id, game_id, quantity) VALUES(?, ?, ?)";
            }

            try(PreparedStatement st = conn.prepareStatement(query)){
                if (customer.getShoppingCart().getCart().containsKey(game)) {
                    st.setInt(1, (customer.getShoppingCart().getCart().get(game) + quantity));
                    st.setInt(2, customer.getId());
                    st.setInt(3, game.getId());
                } else {
                    st.setInt(1, customer.getId());
                    st.setInt(2, game.getId());
                    st.setInt(3, quantity);
                }

                st.executeUpdate();
            }
        }
    }

    @Override
    public void removeGameCart(Customer customer, Game game, int quantity) throws SQLException {
        try(Connection conn = ConnectionManager.getConnection()) {
            assert conn != null;

            String query;
            if (customer.getShoppingCart().getCart().get(game) <= quantity) {
                query = "DELETE FROM cart WHERE user_id = ? AND game_id = ?";
            } else {
                query = "UPDATE cart SET quantity = ? WHERE user_id = ? AND game_id = ?";
            }

            try(PreparedStatement st = conn.prepareStatement(query)){
                if (customer.getShoppingCart().getCart().get(game) <= quantity) {
                    st.setInt(1, customer.getId());
                    st.setInt(2, game.getId());
                } else {
                    st.setInt(1, customer.getShoppingCart().getCart().get(game) - quantity);
                    st.setInt(2, customer.getId());
                    st.setInt(3, game.getId());
                }

                st.executeUpdate();
            }
        }
    }

    @Override
    public void emptyCart(Customer customer) throws SQLException {
        try(Connection conn = ConnectionManager.getConnection()) {
            assert conn != null;

            String query = "DELETE FROM cart WHERE user_id = ?";
            try(PreparedStatement st = conn.prepareStatement(query)){
                st.setInt(1, customer.getId());
                st.executeUpdate();
            }
        }
    }

    @Override
    public int getCustomerRating(Customer customer, Game game) throws SQLException {
        try(Connection conn = ConnectionManager.getConnection()) {
            assert conn != null;

            String query = "SELECT rating FROM customer_rating WHERE customer_id = ? AND game_id = ?";
            try(PreparedStatement st = conn.prepareStatement(query)){
                st.setInt(1, customer.getId());
                st.setInt(2, game.getId());

                try (ResultSet rs = st.executeQuery()) {
                    if(!rs.next())
                        return 0;
                    else
                        return rs.getInt("rating");
                }
            }
        }
    }

    @Override
    public void setCustomerRating(Customer customer, Game game, int rating) throws SQLException {
        String query;
        boolean exists = false;
        int oldRating, numberOfRatings;
        double stars;

        if((oldRating = getCustomerRating(customer, game)) == 0){
            query = "INSERT INTO customer_rating VALUES (?, ?, ?)";
        } else {
            query = "UPDATE customer_rating SET rating = ? WHERE customer_id = ? AND game_id = ?";
            exists = true;
        }

        try(Connection conn = ConnectionManager.getConnection()) {
            assert conn != null;

            try(PreparedStatement st = conn.prepareStatement(query)){

                if(exists){
                    st.setInt(1, rating);
                    st.setInt(2, customer.getId());
                    st.setInt(3, game.getId());
                } else {
                    st.setInt(1, customer.getId());
                    st.setInt(2, game.getId());
                    st.setInt(3, rating);
                }

                st.executeUpdate();
            }

            query = "SELECT * FROM rating WHERE game_id = ?";

            try(PreparedStatement st = conn.prepareStatement(query)){
                st.setInt(1, game.getId());
                try (ResultSet rs = st.executeQuery()) {
                    if(rs.next()){
                        stars = rs.getDouble("stars");
                        numberOfRatings = rs.getInt("number_of_ratings");

                        rs.close();
                        st.close();

                        if(oldRating != 0){
                            query = "UPDATE rating SET stars = ? WHERE game_id = ?";
                        } else {
                            query = "UPDATE rating SET stars = ?, number_of_ratings = ? WHERE game_id = ?";
                        }

                        try(PreparedStatement stmt = conn.prepareStatement(query)) {
                            BigDecimal value = BigDecimal.valueOf(stars).multiply(BigDecimal.valueOf(numberOfRatings));

                            if(oldRating != 0){
                                stmt.setDouble(1, (value.subtract(BigDecimal.valueOf(oldRating))
                                        .add(BigDecimal.valueOf(rating)).divide(BigDecimal.valueOf(numberOfRatings), 10, RoundingMode.FLOOR)).doubleValue());
                                stmt.setInt(2, game.getId());
                            } else {
                                stmt.setDouble(1, (value.add(BigDecimal.valueOf(rating))
                                        .divide(BigDecimal.valueOf(numberOfRatings + 1), 10, RoundingMode.FLOOR)).doubleValue());
                                stmt.setInt(2, numberOfRatings + 1);
                                stmt.setInt(3, game.getId());
                            }

                            stmt.executeUpdate();
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean changePassword(User user, String oldPassword, String newPassword) throws SQLException {
        try (Connection conn = ConnectionManager.getConnection()) {
            assert conn != null;

            try(PreparedStatement st = conn.prepareStatement("SELECT * FROM \"user\" WHERE id = ?")) {
                st.setInt(1, user.getId());

                try (ResultSet rs = st.executeQuery()) {
                    if (!rs.next() || !BCrypt.verifyer().verify(oldPassword.toCharArray(), rs.getString("password")).verified) {
                        return false;
                    }
                }
            }

            try(PreparedStatement st = conn.prepareStatement("UPDATE \"user\" SET password = ? WHERE id = ?")) {
                st.setString(1, BCrypt.withDefaults().hashToString(12, newPassword.toCharArray()));
                st.setInt(2, user.getId());

                st.executeUpdate();
            }
        }

        return true;
    }

    @Override
    public boolean changeCustomerData(Customer customer) throws SQLException {
        Connection conn = ConnectionManager.getConnection();
        assert conn != null;

        try {
            conn.setAutoCommit(false);

            try (PreparedStatement st = conn.prepareStatement("SELECT * FROM \"user\" WHERE id = ?")) {
                st.setInt(1, customer.getId());

                try (ResultSet rs = st.executeQuery()) {
                    if (!rs.next()) {
                        conn.close();
                        return false;
                    }
                }
            }

            try (PreparedStatement st = conn.prepareStatement("UPDATE \"user\" SET email = ? WHERE id = ?")) {
                st.setString(1, customer.getEmail());
                st.setInt(2, customer.getId());

                st.executeUpdate();
            }

            try (PreparedStatement st = conn.prepareStatement("UPDATE customer SET first_name = ?, last_name = ?, phone_number = ? WHERE user_id = ?")) {
                st.setString(1, customer.getFirstName());
                st.setString(2, customer.getLastName());
                st.setString(3, customer.getPhoneNumber());
                st.setInt(4, customer.getId());

                st.executeUpdate();
            }

            conn.commit();

        } catch (SQLException e) {
            conn.rollback();
            return false;
        } finally {
            conn.close();
        }

        return true;
    }
}
