package dao;

import game.Category;
import game.Game;
import game.Rating;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GameDAO implements IGameDAO {
    @Override
    public List<Game> getAllGames() throws SQLException {
        List<Game> games = new ArrayList<>();

        Connection conn = ConnectionManager.getConnection();
        Statement st = Objects.requireNonNull(conn).createStatement();

        String query = "SELECT DISTINCT id, name, publisher, description, price, status, stars, number_of_ratings, image FROM game_view";

        ResultSet rs = st.executeQuery(query);

        while (rs.next()) {
            games.add(new Game(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("publisher"),
                    rs.getString("description"),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    rs.getDouble("price"),
                    rs.getBoolean("status"),
                    rs.getDouble("stars"),
                    rs.getInt("number_of_ratings"),
                    rs.getString("image")));
        }

        for (Game game : games) {
            query = "SELECT DISTINCT platform FROM game_view WHERE id = " + game.getId();
            rs = st.executeQuery(query);

            while (rs.next()) {
                game.getPlatform().add(rs.getString("platform"));
            }

            query = "SELECT DISTINCT category FROM game_view WHERE id = " + game.getId();
            rs = st.executeQuery(query);

            while (rs.next()) {
                game.getCategory().add(Category.getCategory(rs.getString("category")));
            }
        }

        ConnectionManager.closeConnection();

        return games;
    }

    @Override
    public List<Game> getGamesName(String name) throws SQLException {
        List<Game> games = new ArrayList<>();

        Connection conn = ConnectionManager.getConnection();
        Statement st = Objects.requireNonNull(conn).createStatement();

        String query = "SELECT DISTINCT id, name, publisher, description, price, status, stars, number_of_ratings, image " +
                "FROM game_view WHERE name LIKE '%" + name + "%'";

        ResultSet rs = st.executeQuery(query);

        while (rs.next()) {
            games.add(new Game(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("publisher"),
                    rs.getString("description"),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    rs.getDouble("price"),
                    rs.getBoolean("status"),
                    rs.getDouble("stars"),
                    rs.getInt("number_of_ratings"),
                    rs.getString("image")));
        }

        for (Game game : games) {
            query = "SELECT DISTINCT platform FROM game_view WHERE id = " + game.getId();
            rs = st.executeQuery(query);

            while (rs.next()) {
                game.getPlatform().add(rs.getString("platform"));
            }

            query = "SELECT DISTINCT category FROM game_view WHERE id = " + game.getId();
            rs = st.executeQuery(query);

            while (rs.next()) {
                game.getCategory().add(Category.getCategory(rs.getString("category")));
            }
        }

        ConnectionManager.closeConnection();

        return games;
    }

    @Override
    public Game getGame(int id) throws SQLException {
        Connection conn = ConnectionManager.getConnection();
        Statement st = Objects.requireNonNull(conn).createStatement();

        String query = "SELECT id, name, publisher, description, price, status, stars, number_of_ratings, image " +
                "FROM game_view WHERE id = " + id;

        ResultSet rs = st.executeQuery(query);

        if (rs.next()) {
            Game game = new Game(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("publisher"),
                    rs.getString("description"),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    rs.getDouble("price"),
                    rs.getBoolean("status"),
                    rs.getDouble("stars"),
                    rs.getInt("number_of_ratings"),
                    rs.getString("image"));

            query = "SELECT DISTINCT platform FROM game_view WHERE id = " + id;
            rs = st.executeQuery(query);

            while (rs.next()) {
                game.getPlatform().add(rs.getString("platform"));
            }

            query = "SELECT DISTINCT category FROM game_view WHERE id = " + id;
            rs = st.executeQuery(query);
            ConnectionManager.closeConnection();

            while (rs.next()) {
                game.getCategory().add(Category.getCategory(rs.getString("category")));
            }

            return game;
        } else
            return null;
    }

    @Override
    public boolean insertGame(String name, String publisher, String description, List<String> platform, List<String> category, double price, boolean status, Rating rating, String image) throws SQLException {
        Connection conn = ConnectionManager.getConnection();
        Statement st = Objects.requireNonNull(conn).createStatement();

        String query = "SELECT id FROM game WHERE name = '" + name + "';";
        ResultSet rs = st.executeQuery(query);

        if (rs.next())
            return false;

        query = "SELECT id FROM publisher WHERE name = '" + publisher + "';";
        rs = st.executeQuery(query);
        if (!rs.next()) {
            query = "INSERT INTO publisher(name) VALUES ('" + publisher + "')";
            st.executeUpdate(query);
        }

        query = "INSERT INTO \"game\"(name, publisher_id, description, price, status, image) " +
                "VALUES ('" + name + "', " + "(SELECT id FROM publisher WHERE name = '" + publisher + "'), '"
                + description + "', " + price + ", " + status + ", '" + image + "');";
        st.executeUpdate(query);

        for (String c : category) {
            query = "INSERT INTO category_game VALUES ("
                    + "(SELECT id FROM category WHERE name = '" + c + "'), "
                    + "(SELECT id FROM game WHERE name = '" + name + "'));";
            st.executeUpdate(query);
        }

        for (String p : platform) {
            query = "SELECT id FROM platform WHERE name = '" + p + "';";
            rs = st.executeQuery(query);

            if (!rs.next()) {
                query = "INSERT INTO platform(name) VALUES ('" + p + "');";
                st.executeUpdate(query);
            }

            query = "INSERT INTO platform_game VALUES ("
                    + "(SELECT id FROM platform WHERE name = '" + p + "'), "
                    + "(SELECT id FROM game WHERE name = '" + name + "'));";
            st.executeUpdate(query);
        }

        query = "INSERT INTO rating VALUES ("
                + "(SELECT id FROM game WHERE name = '" + name + "'), "
                + rating.getStars() + ", " + rating.getNumberOfRatings() + ");";
        st.executeUpdate(query);

        ConnectionManager.closeConnection();

        return true;
    }

    @Override
    public boolean updateGame(Game game) throws SQLException {
        Connection conn = ConnectionManager.getConnection();
        Statement st = Objects.requireNonNull(conn).createStatement();

        String query = "SELECT id FROM game WHERE id = '" + game.getId() + "';";
        ResultSet rs = st.executeQuery(query);

        if (!rs.next())
            return false;

        query = "UPDATE game SET name = '" + game.getName() + "', "
                + "description = '" + game.getDescription() + "', "
                + "price = '" + game.getPrice() + "', "
                + "status = '" + game.getStatus() + "', "
                + "publisher_id = (SELECT id FROM publisher WHERE publisher.name = '" + game.getPublisher() + "'), "
                + "image = '" + game.getImageName() + "' "
                + "WHERE id = '" + game.getId() + "';";
        st.executeUpdate(query);

        query = "UPDATE rating SET stars = " + game.getRating().getStars() + ", "
                + "number_of_ratings = " + game.getRating().getNumberOfRatings() + ", "
                + "WHERE game_id = " + game.getId();
        st.executeUpdate(query);

        query = "DELETE FROM platform_game WHERE game_id = '" + game.getId() + "';";
        st.executeUpdate(query);

        for (String p : game.getPlatform()) {
            query = "SELECT id FROM platform WHERE name = '" + p + "';";
            rs = st.executeQuery(query);

            if (!rs.next()) {
                query = "INSERT INTO platform(name) VALUES ('" + p + "');";
                st.executeUpdate(query);
            }

            query = "INSERT INTO platform_game VALUES ("
                    + "(SELECT id FROM platform WHERE name = '" + p + "'), "
                    + game.getId() + ");";
            st.executeUpdate(query);
        }

        query = "DELETE FROM category_game WHERE game_id = '" + game.getId() + "';";
        st.executeUpdate(query);

        for (Category c : game.getCategory()) {
            query = "INSERT INTO category_game VALUES ("
                    + "(SELECT id FROM category WHERE name = '" + c.toString() + "'), "
                    + game.getId() + ");";
            st.executeUpdate(query);
        }

        ConnectionManager.closeConnection();

        return true;
    }

    public static void main(String[] args) {
        try {
            new GameDAO().insertGame("FNAF: Security Breach", "ScottGames",
                    "In Five Nights at Freddy’s: Security Breach, play as Gregory, a young boy who’s been trapped overnight inside of Freddy Fazbear’s Mega Pizzaplex.",
                    new ArrayList<>() {{
                        add("PC");
                    }}, new ArrayList<>() {{
                        add("Action");
                        add("Strategy");
                        add("Adventure");
                    }}, 33.99, false, new Rating(4.8, 7), "fnaf.jpg");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
