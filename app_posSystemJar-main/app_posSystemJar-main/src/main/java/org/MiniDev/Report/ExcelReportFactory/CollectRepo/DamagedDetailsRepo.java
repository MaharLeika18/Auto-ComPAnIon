package org.MiniDev.Report.ExcelReportFactory.CollectRepo;

import DBConnection.DBConnection;
import org.MiniDev.Report.ExcelReportFactory.CollectModel.DamagedDetailsModel;
import org.MiniDev.Report.ExcelReportFactory.CollectService.DamagedDetailsService;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DamagedDetailsRepo {
    private final Logger LOGGER = Logger.getLogger(DamagedDetailsService.class.getName());

    public List<DamagedDetailsModel> getDamagedAmountLists() {
        String sql = "SELECT\n" +
                "\teth.exp_date AS ReportDate,\n" +
                "\teth.exp_code AS ExpCode,\n" +
                "\tec.exp_code_name AS ExpCodeName,\n" +
                "\temp1.e_name AS AssignedToEMP,\n" +
                "\teth.last_updated_date AS LastUpdateDate,\n" +
                "\temp.e_name AS EntryEMP,\n" +
                "\tIFNULL(\n" +
                "\t\tSUM(eth.exp_amount - eth.refund_amount),\n" +
                "\t\t0\n" +
                "\t) AS DamagedAmount\n" +
                "FROM\n" +
                "\texp_trn_hist eth\n" +
                "LEFT JOIN exp_codes ec on ec.exp_code = eth.exp_code\n" +
                "LEFT JOIN hr_emp_lists emp on emp.e_id = eth.teller_id\n" +
                "LEFT JOIN hr_emp_lists emp1 on emp1.e_id = eth.assigned_to_emp\n" +
                "WHERE\n" +
                "\teth.trn_cancel_yn = 'N'\n" +
                "\tAND eth.exp_code = 'EXP024'\n" +
                "GROUP BY\n" +
                "\teth.exp_date ,\n" +
                "\teth.exp_code ,\n" +
                "\tec.exp_code_name ,\n" +
                "\teth.assigned_to_emp ,\n" +
                "\teth.last_updated_date ,\n" +
                "\temp.e_name;";

        List<DamagedDetailsModel> damagedDetailsModelList = new ArrayList<>(); // Initialize the list

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Date reportDate = rs.getDate("ReportDate");
                String expCode = rs.getString("ExpCode");
                String expCodeName = rs.getString("ExpCodeName");
                String assignedToEmp = rs.getString("AssignedToEMP");
                Timestamp lastUpdateDate = rs.getTimestamp("LastUpdateDate");
                String entryEmp = rs.getString("EntryEMP");
                double damagedAmount = rs.getDouble("DamagedAmount");

                DamagedDetailsModel damagedDetailsModel = new DamagedDetailsModel(reportDate, expCode,expCodeName,assignedToEmp,lastUpdateDate,entryEmp,damagedAmount);
                damagedDetailsModelList.add(damagedDetailsModel);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching sets", e);
        }
        return damagedDetailsModelList;
    }
}
