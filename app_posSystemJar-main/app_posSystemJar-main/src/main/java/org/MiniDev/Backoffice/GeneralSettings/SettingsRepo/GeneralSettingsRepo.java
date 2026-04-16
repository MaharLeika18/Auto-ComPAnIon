package org.MiniDev.Backoffice.GeneralSettings.SettingsRepo;

import DBConnection.DBConnection;
import org.MiniDev.Backoffice.GeneralSettings.SettingsModel.GeneralSettingsModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GeneralSettingsRepo {

    private static final Logger LOGGER = Logger.getLogger(GeneralSettingsRepo.class.getName());

    public GeneralSettingsModel getGeneralSettingsModel() {
        String sql = "SELECT * FROM business_settings LIMIT 1"; // Assuming only one row

        GeneralSettingsModel generalSettingsModel = null;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                generalSettingsModel = new GeneralSettingsModel(
                        rs.getString("biz_name"),
                        rs.getString("biz_address"),
                        rs.getString("biz_towncity"),
                        rs.getString("biz_phno"),
                        rs.getString("biz_mail"),
                        rs.getString("tax_cal_YN").charAt(0),
                        rs.getDouble("tax_sales_rate"),
                        rs.getString("cash_YN").charAt(0),
                        rs.getString("bank_YN").charAt(0),
                        rs.getString("mobilepay_YN").charAt(0),
                        rs.getString("giftcard_YN").charAt(0),
                        rs.getString("admin_access_YN").charAt(0),
                        rs.getString("manager_access_YN").charAt(0),
                        rs.getString("cashier_access_YN").charAt(0),
                        rs.getString("stock_tracking_YN").charAt(0),
                        rs.getInt("lowstock_num_alert"),
                        rs.getString("auto_reorder_YN").charAt(0),
                        rs.getString("receipt_header"),
                        rs.getString("receipt_footer"),
                        rs.getString("itemized_receipt_YN").charAt(0),
                        rs.getString("print_receipt_auto_YN").charAt(0),
                        rs.getString("noti_email_YN").charAt(0),
                        rs.getString("noti_sms_YN").charAt(0),
                        rs.getString("table_need_YN").charAt(0)
                );
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching general settings", e);
        }

        return generalSettingsModel;
    }
}
