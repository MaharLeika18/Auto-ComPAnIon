//package DBConnection;
//
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.SQLException;
//
//public class TestMySQLConnection {
//
//    public static void main(String[] args) {
//        String url = "jdbc:mysql://10.0.2.15:3306/posdb"; // Replace with your database name
//        String user = "cloudposvps"; // Replace with your username
//        String password = "reMote@#!2024POS"; // Replace with your password
//
//        Connection connection = null;
//
//        try {
//            // Load the MySQL JDBC driver
//            Class.forName("com.mysql.cj.jdbc.Driver");
//
//            // Establish the connection
//            connection = DriverManager.getConnection(url, user, password);
//
//            // If the connection was successful
//            if (connection != null) {
//                System.out.println("Connection successful!");
//            } else {
//                System.out.println("Failed to make connection!");
//            }
//        } catch (SQLException e) {
//            System.out.println("SQL Exception: " + e.getMessage());
//            e.printStackTrace();
//        } catch (ClassNotFoundException e) {
//            System.out.println("Class Not Found Exception: " + e.getMessage());
//            e.printStackTrace();
//        } finally {
//            // Close the connection
//            try {
//                if (connection != null && !connection.isClosed()) {
//                    connection.close();
//                }
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//}
