package dao;

import game.Category;
import game.Game;

import java.sql.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class GameDAO implements IGameDAO {
    /**
     *
     */
    @Override
    public List<Game> getAllGames() throws SQLException {
        List<Game> games = new ArrayList<>();

        try(Connection conn = ConnectionManager.getConnection()) {
            assert conn != null;

            String query = "SELECT DISTINCT id, name, publisher, description, price, status, stars, number_of_ratings, image FROM game_view";
            try (Statement st = conn.createStatement()) {
                getGameData(games, st.executeQuery(query));
            }

            getPlatformCategory(games, conn);
        }

        return games;
    }

    /**
     *
     */
    @Override
    public List<Game> getGamesName(String name) throws SQLException {
        List<Game> games = new ArrayList<>();

        try(Connection conn = ConnectionManager.getConnection()) {
            assert conn != null;

            String query = "SELECT DISTINCT id, name, publisher, description, price, status, stars, " +
                    "number_of_ratings, image FROM game_view WHERE name LIKE ?";

            try(PreparedStatement st = conn.prepareStatement(query)){
                st.setString(1, "%" + name + "%");
                getGameData(games, st.executeQuery());
            }

            getPlatformCategory(games, conn);
        }

        return games;
    }

    /**
     *
     */
    @Override
    public Game getGame(int id) throws SQLException {
        Game game = null;

        try(Connection conn = ConnectionManager.getConnection()) {
            assert conn != null;

            String query = "SELECT id, name, publisher, description, price, status, stars, " +
                    "number_of_ratings, image FROM game_view WHERE id = ?";

            try(PreparedStatement st = conn.prepareStatement(query)) {
                st.setInt(1, id);
                try (ResultSet rs = st.executeQuery()) {
                    if (rs.next()) {
                        game = new Game(
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
                    }
                }
            }

            try (PreparedStatement st = conn.prepareStatement("SELECT DISTINCT platform FROM game_view WHERE id = ?")) {
                st.setInt(1, id);
                try (ResultSet rs = st.executeQuery()) {
                    while (rs.next()) {
                        assert game != null;
                        game.getPlatform().add(rs.getString("platform"));
                    }
                }
            }

            try (PreparedStatement st = conn.prepareStatement("SELECT DISTINCT category FROM game_view WHERE id = ?")) {
                st.setInt(1, id);
                try (ResultSet rs = st.executeQuery()) {
                    while (rs.next()) {
                        assert game != null;
                        game.getCategory().add(Category.getCategory(rs.getString("category")));
                    }
                }
            }

        }

        return game;
    }

    /**
     *
     */
    @Override
    public Game insertGame(Game game) throws SQLException {
        Connection conn = ConnectionManager.getConnection();
        assert conn != null;

        int id = game.getId();

        try {
            conn.setAutoCommit(false);

            try (PreparedStatement st = conn.prepareStatement("SELECT id FROM game WHERE id = ?")) {
                st.setInt(1, id);
                try (ResultSet rs = st.executeQuery()) {
                    if (rs.next())
                        return null;
                }
            }

            setPublisher(game, conn);

            String query = "INSERT INTO \"game\"(name, publisher_id, description, price, status, image) " +
                    "VALUES (?, (SELECT id FROM publisher WHERE name = ?), ?, ?, ?, ?)";

            try (PreparedStatement st = conn.prepareStatement(query)) {
                st.setString(1, game.getName());
                st.setString(2, game.getPublisher());
                st.setString(3, game.getDescription());
                st.setDouble(4, game.getPrice());
                st.setBoolean(5, game.getStatus());
                st.setString(6, game.getImageName());

                st.executeUpdate();
            }

            try (PreparedStatement st = conn.prepareStatement("SELECT id FROM game WHERE name = ?")) {
                st.setString(1, game.getName());
                try (ResultSet rs = st.executeQuery()) {
                    if(rs.next())
                        id = rs.getInt("id");
                }
            }

            for (Category category : game.getCategory()) {
                query = "INSERT INTO category_game VALUES ("
                        + "(SELECT id FROM category WHERE name = ?), "
                        + "(SELECT id FROM game WHERE name = ?))";

                try (PreparedStatement st = conn.prepareStatement(query)) {
                    st.setString(1, category.toString());
                    st.setString(2, game.getName());

                    st.executeUpdate();
                }
            }

            for (String platform : game.getPlatform()) {
                setPlatform(id, conn, platform);
            }

            query = "INSERT INTO rating VALUES ((SELECT id FROM game WHERE name = ?), ?, ?)";
            try (PreparedStatement st = conn.prepareStatement(query)) {
                st.setString(1, game.getName());
                st.setDouble(2, game.getRating().getStars());
                st.setDouble(3, game.getRating().getNumberOfRatings());
                st.executeUpdate();
            }
            conn.commit();

        } catch (SQLException e) {
            conn.rollback();
        } finally {
            conn.close();
        }

        return getGame(id);
    }

    /**
     *
     */
    @Override
    public Game updateGame(Game game) throws SQLException {
        Connection conn = ConnectionManager.getConnection();
        assert conn != null;

        try {
            conn.setAutoCommit(false);

            try (PreparedStatement st = conn.prepareStatement("SELECT id FROM game WHERE id = ?")) {
                st.setInt(1, game.getId());
                try (ResultSet rs = st.executeQuery()) {
                    if (!rs.next())
                        return null;
                }
            }

            setPublisher(game, conn);

            String query = "UPDATE game SET name = ?, description = ?, price = ?, status = ?, "
                    + "publisher_id = (SELECT id FROM publisher WHERE publisher.name = ?), "
                    + "image = ? WHERE id = ?";

            try (PreparedStatement st = conn.prepareStatement(query)) {
                st.setString(1, game.getName());
                st.setString(2, game.getDescription());
                st.setDouble(3, game.getPrice());
                st.setBoolean(4, game.getStatus());
                st.setString(5, game.getPublisher());
                st.setString(6, game.getImageName());
                st.setInt(7, game.getId());

                st.executeUpdate();
            }

            try (PreparedStatement st = conn.prepareStatement("DELETE FROM platform_game WHERE game_id = ?")) {
                st.setInt(1, game.getId());
                st.executeUpdate();
            }

            for (String platform : game.getPlatform()) {
                try (PreparedStatement st = conn.prepareStatement("DELETE FROM platform_game WHERE game_id = ?")) {
                    st.setInt(1, game.getId());
                    st.executeUpdate();
                }

                setPlatform(game.getId(), conn, platform);
            }

            try (PreparedStatement st = conn.prepareStatement("DELETE FROM category_game WHERE game_id = ?")) {
                st.setInt(1, game.getId());
                st.executeUpdate();
            }

            for (Category category : game.getCategory()) {
                query = "INSERT INTO category_game VALUES ((SELECT id FROM category WHERE name = ?), ?)";
                try (PreparedStatement st = conn.prepareStatement(query)) {
                    st.setString(1, category.toString());
                    st.setInt(2, game.getId());
                    st.executeUpdate();
                }
            }

            query = "INSERT INTO rating VALUES ((SELECT id FROM game WHERE name = ?), ?, ?)";
            try (PreparedStatement st = conn.prepareStatement(query)) {
                st.setString(1, game.getName());
                st.setDouble(2, game.getRating().getStars());
                st.setDouble(3, game.getRating().getNumberOfRatings());
                st.executeUpdate();
            }
            conn.commit();

        } catch (SQLException e) {
            conn.rollback();
            return null;
        } finally{
            conn.close();
        }

        return getGame(game.getId());
    }

    /**
     *
     */
    private void setPlatform(int id, Connection conn, String platform) throws SQLException {
        boolean exists = true;
        String query;

        try (PreparedStatement st = conn.prepareStatement("SELECT id FROM platform WHERE name = ?")) {
            st.setString(1, platform);
            try (ResultSet rs = st.executeQuery()) {
                if (!rs.next()) {
                    exists = false;
                }
            }
        }

        if(!exists){
            try (PreparedStatement st = conn.prepareStatement("INSERT INTO platform(name) VALUES (?)")) {
                st.setString(1, platform);
                st.executeUpdate();
            }
        }

        query = "INSERT INTO platform_game VALUES ((SELECT id FROM platform WHERE name = ?), ?)";
        try (PreparedStatement st = conn.prepareStatement(query)) {
            st.setString(1, platform);
            st.setInt(2, id);
            st.executeUpdate();
        }
    }

    /**
     *
     */
    private void setPublisher(Game game, Connection conn) throws SQLException {
        boolean exists = true;

        try (PreparedStatement st = conn.prepareStatement("SELECT id FROM publisher WHERE name = ?")) {
            st.setString(1, game.getPublisher());
            try (ResultSet rs = st.executeQuery()) {
                if (!rs.next())
                    exists = false;
            }
        }

        if (!exists) {
            try (PreparedStatement st = conn.prepareStatement("INSERT INTO publisher(name) VALUES (?)")) {
                st.setString(1, game.getPublisher());
                st.executeUpdate();
            }
        }
    }

    /**
     *
     */
    private void getGameData(List<Game> games, ResultSet resultSet) throws SQLException {
        try(ResultSet rs = resultSet){
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
        }
    }

    /**
     *
     */
    private void getPlatformCategory(List<Game> games, Connection conn) throws SQLException {
        for (Game game : games) {
            try (PreparedStatement st = conn.prepareStatement("SELECT DISTINCT platform FROM game_view WHERE id = ?")) {
                st.setInt(1, game.getId());
                try (ResultSet rs = st.executeQuery()) {
                    while (rs.next())
                        game.getPlatform().add(rs.getString("platform"));
                }
            }
        }

        for (Game game : games) {
            try (PreparedStatement st = conn.prepareStatement("SELECT DISTINCT category FROM game_view WHERE id = ?")) {
                st.setInt(1, game.getId());
                try (ResultSet rs = st.executeQuery()) {
                    while (rs.next())
                        game.getCategory().add(Category.getCategory(rs.getString("category")));
                }
            }
        }
    }

    /**
     *
     */
    @Override
    public List<String> getPlatforms() throws SQLException {
        List<String> platforms = new ArrayList<>();

        try(Connection conn = ConnectionManager.getConnection()) {
            assert conn != null;

            try (Statement st = conn.createStatement()) {
                String query = "SELECT name FROM platform ORDER BY name";
                try (ResultSet rs = st.executeQuery(query)) {
                    while (rs.next()) {
                        platforms.add(rs.getString(1));
                    }
                }
            }
        }

        return platforms;
    }

    /**
     *
     */
    @Override
    public List<String> getPublishers() throws SQLException {
        List<String> publishers = new ArrayList<>();

        try(Connection conn = ConnectionManager.getConnection()) {
            assert conn != null;

            try (Statement st = conn.createStatement()) {
                String query = "SELECT name FROM publisher ORDER BY name";
                try (ResultSet rs = st.executeQuery(query)) {
                    while (rs.next()) {
                        publishers.add(rs.getString(1));
                    }
                }
            }
        }

        return publishers;
    }

    public static void main(String[] args) {
        try {
            new GameDAO().insertGame(new Game(0, "Red Dead Redemption 2", "Rockstar",
                    "Winner of over 175 Game of the Year Awards and recipient of over 250 perfect scores, RDR2 is the epic tale of outlaw Arthur Morgan and the infamous Van der Linde gang, on the run across America at the dawn of the modern age.",
                    new ArrayList<>() {{
                        add("PC");
                        add("PS5");
                        add("Xbox Series X");
                    }},
                    new ArrayList<>() {{
                        add(Category.ACTION.toString());
                        add(Category.ADVENTURE.toString());
                    }}, 60.0, true,0, 0, "rdr2.jpg"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
