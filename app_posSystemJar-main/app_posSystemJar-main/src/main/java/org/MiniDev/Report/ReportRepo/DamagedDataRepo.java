package org.MiniDev.Report.ReportRepo;

import DBConnection.DBConnection;
import org.MiniDev.Report.ReportModel.DamagedDataModel;
import java.math.BigDecimal;
import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DamagedDataRepo {

    private final Logger LOGGER = Logger.getLogger(DamagedDataRepo.class.getName());

    public Set<DamagedDataModel> getDamagedDataModelSet() {
        String sql = "SELECT exp_date as ReportDate,\n" +
                "       ifnull(sum(exp_amount - refund_amount), 0) AS DamagedAmount\n" +
                "FROM exp_trn_hist\n" +
                "WHERE trn_cancel_yn = 'N'\n" +
                "  AND exp_code = 'EXP024'\n" +
                "GROUP BY exp_date;";

        Set<DamagedDataModel> damagedDataModelsSet = new HashSet<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Date reportDate = rs.getDate("ReportDate");
                BigDecimal damagedAmount = rs.getBigDecimal("DamagedAmount");

                DamagedDataModel damagedDataModel = new DamagedDataModel(reportDate, damagedAmount);
                damagedDataModelsSet.add(damagedDataModel);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching sets", e);
        }
        return damagedDataModelsSet;
    }
}
