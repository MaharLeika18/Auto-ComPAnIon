package org.MiniDev.Report.ReportRepo;

import DBConnection.DBConnection;
import org.MiniDev.Report.ReportModel.ProfitChartDataModel;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ChartDataRepo {

    private final Logger LOGGER = Logger.getLogger(ChartDataRepo.class.getName());

    public List<ProfitChartDataModel> getProfitChartDataModelLists() {
        String sql = "WITH RECURSIVE\n" +
                "    date_range AS (SELECT DATE_FORMAT(NOW(), '%Y-%m-01') AS ReportDate -- First day of the current month\n" +
                "                   UNION ALL\n" +
                "                   SELECT DATE_ADD(ReportDate, INTERVAL 1 DAY)\n" +
                "                   FROM date_range\n" +
                "                   WHERE ReportDate < LAST_DAY(\n" +
                "                           NOW()) -- Last day of the current month\n" +
                "\n" +
                "    ),\n" +
                "    pf AS (SELECT CAST(ol.StartDate AS DATE)                                  AS OrderDate,\n" +
                "                  ifnull(SUM(oh.OrderSubTotal) - SUM(oh.OriginalSubTotal), 0) AS ProfitAmt\n" +
                "           FROM Order_Hist oh\n" +
                "                    LEFT JOIN Order_Lists ol ON ol.OrderCode = oh.OrderCode\n" +
                "           GROUP BY CAST(ol.StartDate AS DATE))\n" +
                "SELECT dr.ReportDate,\n" +
                "       ifnull(sum(et.exp_amount), 0)                                               AS exp_amt,\n" +
                "       ifnull(sum(pf.ProfitAmt), 0)                                                AS income_amt,\n" +
                "       if(ifnull(ifnull(sum(pf.ProfitAmt), 0) - ifnull(sum(et.exp_amount), 0), 0) < 0, 0,\n" +
                "          ifnull(ifnull(sum(pf.ProfitAmt), 0) - ifnull(sum(et.exp_amount), 0), 0)) AS profit_amt\n" +
                "FROM date_range dr\n" +
                "         LEFT JOIN exp_trn_hist et ON et.exp_date = dr.ReportDate AND et.trn_cancel_yn = 'N'\n" +
                "         LEFT JOIN pf ON pf.OrderDate = dr.ReportDate\n" +
                "GROUP BY dr.ReportDate;";

        List<ProfitChartDataModel> profitChartDataModelLists = new ArrayList<>(); // Initialize the list

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Date reportDate = rs.getDate("ReportDate");
                BigDecimal expAmount = rs.getBigDecimal("exp_amt");
                BigDecimal incomeAmount = rs.getBigDecimal("income_amt");
                BigDecimal profitAmount = rs.getBigDecimal("profit_amt");

                ProfitChartDataModel profitChartData = new ProfitChartDataModel(reportDate, expAmount, incomeAmount, profitAmount);
                profitChartDataModelLists.add(profitChartData);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching lists", e);
        }
        return profitChartDataModelLists;
    }
}
