package org.MiniDev.Report.ReportRepo;

import DBConnection.DBConnection;
import org.MiniDev.Report.ReportModel.OutOfStockModel;
import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OutOfStockRepo {

    private final Logger LOGGER = Logger.getLogger(OutOfStockRepo.class.getName());

    public Set<OutOfStockModel> getOutOfStockSet() {
        String sql = "select * from stock_out_of;";

        Set<OutOfStockModel> outOfStockModelSet = new HashSet<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Date outDate = rs.getDate("out_date");
                String outItemName = rs.getString("out_of_item");

                OutOfStockModel outOfStockModel = new OutOfStockModel(outDate, outItemName);
                outOfStockModelSet.add(outOfStockModel);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching sets", e);
        }
        return outOfStockModelSet;
    }
}
