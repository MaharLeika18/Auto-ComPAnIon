package DBConnection;//package DBConnection;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnection {

    private static String URL;
    private static String USER;
    private static String PASSWORD;
    private static String DRIVER;

    // Initialize connection parameters from config file
    static {
        Properties properties = new Properties();
        try (
                // Load the config.properties file from the classpath
                InputStream input = DBConnection.class.getClassLoader().getResourceAsStream("config.properties")) {

            if (input == null) {
                System.err.println("Unable to find config.properties file.");
                throw new IOException("Config file not found in the classpath");
            }

            // Load the properties file
            properties.load(input);

            // Read the properties values
            URL = properties.getProperty("db.url");
            USER = properties.getProperty("db.user");
            PASSWORD = properties.getProperty("db.password");
            DRIVER = properties.getProperty("db.driver");

            // Load the JDBC driver
            Class.forName(DRIVER);
        } catch (IOException e) {
            System.err.println("Error loading config.properties file.");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.err.println("MariaDB JDBC Driver not found.");
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        // Return a new connection to the database
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}



//import java.io.IOException;
//import java.io.InputStream;
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.SQLException;
//import java.util.Properties;
//
//public class DBConnection {
//
//    private static String URL;
//    private static String USER;
//    private static String PASSWORD;
//    private static String DRIVER;
//
//    // Initialize connection parameters from config file
//    static {
//        Properties properties = new Properties();
//        try (InputStream input = DBConnection.class.getClassLoader().getResourceAsStream("config.properties")) {
//            if (input == null) {
//                System.err.println("Unable to find config.properties file.");
//                throw new IOException("Config file not found");
//            }
//
//            // Load the properties file
//            properties.load(input);
//
//            // Read the properties values
//            URL = properties.getProperty("db.url");
//            USER = properties.getProperty("db.user");
//            PASSWORD = properties.getProperty("db.password");
//            DRIVER = properties.getProperty("db.driver");
//
//            // Load the MySQL driver
//            Class.forName(DRIVER);
//        } catch (IOException | ClassNotFoundException e) {
//            System.err.println("Error loading config or JDBC Driver.");
//            e.printStackTrace();
//        }
//    }
//
//    public static Connection getConnection() throws SQLException {
//        // Return a new connection to the database
//        return DriverManager.getConnection(URL, USER, PASSWORD);
//    }
//}
