//package org.MiniDev.Login;
//
//import DBConnection.DBConnection;
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.SQLException;
//
//public class PasswordInsertExample {
//    public static void insertTeller(String tellerName, String tellerAddress, String tellerIP, String tellerMachine, String rawPassword) {
//        String hashedPassword = PasswordUtils.hashPassword(rawPassword);
//
//        String sql = "INSERT INTO Teller (TellerName, TellerAddress, TellerIP, TellerMachine, TellerPassword, LastChangedDate, LastLoginDate, LastLogoutDate, CreatedAt, UpdatedAt) " +
//                "VALUES (?, ?, ?, ?, ?, CURDATE(), NOW(), NULL, NOW(), NOW())";
//
//        try (Connection connection = DBConnection.getConnection();
//             PreparedStatement stmt = connection.prepareStatement(sql)) {
//
//            stmt.setString(1, tellerName);
//            stmt.setString(2, tellerAddress);
//            stmt.setString(3, tellerIP);
//            stmt.setString(4, tellerMachine);
//            stmt.setString(5, hashedPassword);
//
//            int rowsAffected = stmt.executeUpdate();
//            System.out.println("Rows affected: " + rowsAffected);
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public static void main(String[] args) {
//        insertTeller("admin","Mandalay","10.11.1.1","Local","admin");
//    }
//}
