package dao;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * ConnectionManager is a utility class that manages
 * the connection to the database.
 *
 * @author      Marius Bubui
 * @version     1.0
 */
public class ConnectionManager {
    /**
     * The location of the file containing the configurations.
     */
    private static final String properties = "src/main/resources/dbconfig.properties";

    /**
     * Methode that generates a connection
     * with thr database.
     *
     * @return the new connection object or null
     */
    public static Connection getConnection() {
        Connection connection;
        try (InputStream stream = new FileInputStream(properties)) {

            Properties prop = new Properties();
            prop.load(stream);
            Class.forName(prop.getProperty("db.driver"));
            connection = DriverManager.getConnection(prop.getProperty("db.url"), prop.getProperty("db.username"), prop.getProperty("db.password"));

        } catch (ClassNotFoundException | IOException | SQLException e) {
            return null;
        }
        return connection;
    }
}
