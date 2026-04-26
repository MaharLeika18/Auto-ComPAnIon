package org.MiniDev.Report.ReportRepo;

import DBConnection.DBConnection;
import org.MiniDev.Report.ReportModel.DamagedDataModel;
import org.MiniDev.Report.ReportModel.TransactionCntModel;

import java.math.BigDecimal;
import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TransactionCntRepo {

    private final Logger LOGGER = Logger.getLogger(TransactionCntRepo.class.getName());

    public Set<TransactionCntModel> getTransactionCntSet() {
        String sql = "select * from db_trn_count;";

        Set<TransactionCntModel> transactionCntModelSet = new HashSet<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Date trnDate = rs.getDate("trn_date");
                int trnCnt = rs.getInt("update_cnt");

                TransactionCntModel transactionCntModel = new TransactionCntModel(trnDate, trnCnt);
                transactionCntModelSet.add(transactionCntModel);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching sets", e);
        }
        return transactionCntModelSet;
    }
}
