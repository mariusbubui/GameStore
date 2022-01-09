package dao;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionManager {
    private static final String properties = "src/main/resources/dbconfig.properties";
    private static Connection connection;

    public static Connection getConnection() {
        try (InputStream input = new FileInputStream(properties)) {
            Properties prop = new Properties();
            prop.load(input);

            Class.forName(prop.getProperty("db.driver"));
            try {
                connection = DriverManager.getConnection(prop.getProperty("db.url"), prop.getProperty("db.username"), prop.getProperty("db.password"));
            } catch (SQLException e) {
                System.out.println("Failed to create the database connection.");
                return null;
            }
        } catch (ClassNotFoundException e) {
            System.out.println("Driver not found.");
            return null;
        } catch (IOException e) {
            //e.printStackTrace();
        }
        return connection;
    }

    public static void closeConnection() throws SQLException {
        connection.close();
    }
}
