package com.autocompanion.pos.fx;  // matches file location: pos/fx/PaiController.java

import java.io.IOException;
import java.net.URL;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.Month;

import org.MiniDev.DBConnection.DBConnection;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class PaiController {

    // ── Metric value labels ───────────────────────────────────────────────────
    @FXML private Label ebitdaValueLabel;
    @FXML private Label grossProfitValueLabel;
    @FXML private Label netProfitValueLabel;

    // ── Month checkboxes ──────────────────────────────────────────────────────
    @FXML private CheckBox cbJan, cbFeb, cbMar, cbApr, cbMay, cbJun;
    @FXML private CheckBox cbJul, cbAug, cbSep, cbOct, cbNov, cbDec;
    @FXML private CheckBox cbAllMonths;

    // ── Quarter checkboxes ────────────────────────────────────────────────────
    @FXML private CheckBox cbQ1, cbQ2, cbQ3, cbQ4;
    @FXML private CheckBox cbAllQuarters;

    // ── Year checkboxes ───────────────────────────────────────────────────────
    @FXML private CheckBox cb2024, cb2025, cb2026, cb2027, cb2028;
    @FXML private CheckBox cbAllYears;

    // ── FXML paths ────────────────────────────────────────────────────────────
    private static final String FXML_BASE      = "/com/autocompanion/pos/fx/";
    private static final String FXML_DASHBOARD = FXML_BASE + "Dashboard.fxml";
    private static final String FXML_INVENTORY = FXML_BASE + "Inventory.fxml";

    private final DecimalFormat df     = new DecimalFormat("#,###.00");
    private final DecimalFormat pctFmt = new DecimalFormat("#,##0.00");

    private boolean initializing = true;

    // ─────────────────────────────────────────────────────────────────────────
    // INIT
    // ─────────────────────────────────────────────────────────────────────────
    @FXML
    private void initialize() {
        initializing = true;

        cb2026.setSelected(true);
        cbAllMonths.setSelected(true);
        setAllMonths(true);
        cbAllQuarters.setSelected(true);
        setAllQuarters(true);
        cbAllYears.setSelected(false);

        initializing = false;
        loadMetrics();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // FILTER CHANGE — all checkboxes route here via onAction="#onFilterChange"
    // ─────────────────────────────────────────────────────────────────────────
    @FXML
    private void onFilterChange(ActionEvent event) {
        if (initializing) return;

        Object src = event.getSource();

        if (src == cbAllMonths)   setAllMonths(cbAllMonths.isSelected());
        if (src == cbAllQuarters) setAllQuarters(cbAllQuarters.isSelected());
        if (src == cbAllYears)    setAllYears(cbAllYears.isSelected());

        if (src == cbQ1) syncMonthsFromQuarter(cbQ1, 0);
        if (src == cbQ2) syncMonthsFromQuarter(cbQ2, 3);
        if (src == cbQ3) syncMonthsFromQuarter(cbQ3, 6);
        if (src == cbQ4) syncMonthsFromQuarter(cbQ4, 9);

        syncAllToggleState(cbAllMonths,   monthBoxes());
        syncAllToggleState(cbAllQuarters, quarterBoxes());
        syncAllToggleState(cbAllYears,    yearBoxes());

        loadMetrics();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // TOGGLE HELPERS
    // ─────────────────────────────────────────────────────────────────────────
    private void setAllMonths(boolean v)   { for (CheckBox cb : monthBoxes())   cb.setSelected(v); }
    private void setAllQuarters(boolean v) { for (CheckBox cb : quarterBoxes()) cb.setSelected(v); if (v) setAllMonths(true); }
    private void setAllYears(boolean v)    { for (CheckBox cb : yearBoxes())    cb.setSelected(v); }

    private void syncMonthsFromQuarter(CheckBox qtr, int offset) {
        CheckBox[] m = monthBoxes();
        boolean v = qtr.isSelected();
        m[offset].setSelected(v);
        m[offset + 1].setSelected(v);
        m[offset + 2].setSelected(v);
    }

    private void syncAllToggleState(CheckBox allBox, CheckBox[] children) {
        long sel = 0;
        for (CheckBox cb : children) if (cb.isSelected()) sel++;
        if (sel == children.length)  { allBox.setIndeterminate(false); allBox.setSelected(true);  }
        else if (sel == 0)           { allBox.setIndeterminate(false); allBox.setSelected(false); }
        else                         { allBox.setIndeterminate(true);                             }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // DATE RANGE
    // ─────────────────────────────────────────────────────────────────────────
    private LocalDate[] getDateRange() {
        int[] yearVals = {2024, 2025, 2026, 2027, 2028};
        CheckBox[] ybs = yearBoxes();
        int minY = Integer.MAX_VALUE, maxY = Integer.MIN_VALUE;
        for (int i = 0; i < ybs.length; i++) {
            if (ybs[i].isSelected()) { minY = Math.min(minY, yearVals[i]); maxY = Math.max(maxY, yearVals[i]); }
        }
        if (minY == Integer.MAX_VALUE) return null;

        CheckBox[] mbs = monthBoxes();
        int minM = Integer.MAX_VALUE, maxM = Integer.MIN_VALUE;
        for (int i = 0; i < mbs.length; i++) {
            if (mbs[i].isSelected()) { minM = Math.min(minM, i + 1); maxM = Math.max(maxM, i + 1); }
        }
        if (minM == Integer.MAX_VALUE) return null;

        return new LocalDate[]{
            LocalDate.of(minY, minM, 1),
            LocalDate.of(maxY, maxM, Month.of(maxM).length(java.time.Year.isLeap(maxY)))
        };
    }

    // ─────────────────────────────────────────────────────────────────────────
    // LOAD METRICS
    // ─────────────────────────────────────────────────────────────────────────
    private void loadMetrics() {
        LocalDate[] range = getDateRange();
        if (range == null) {
            ebitdaValueLabel.setText("—");
            grossProfitValueLabel.setText("—");
            netProfitValueLabel.setText("—");
            return;
        }

        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall("CALL calculate_roi(?, ?)")) {

            cs.setString(1, range[0] + " 00:00:00");
            cs.setString(2, range[1] + " 23:59:59");

            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) {
                    double revenue     = rs.getDouble("total_revenue");
                    double cogs        = rs.getDouble("total_cost");
                    double grossProfit = revenue - cogs;
                    double netProfit   = rs.getDouble("net_profit");
                    double ebitda      = revenue != 0 ? (netProfit / revenue) * 100 : 0;

                    ebitdaValueLabel.setText(pctFmt.format(ebitda) + "%");
                    grossProfitValueLabel.setText("₱" + df.format(grossProfit));
                    netProfitValueLabel.setText("₱" + df.format(netProfit));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            ebitdaValueLabel.setText("ERR");
            grossProfitValueLabel.setText("ERR");
            netProfitValueLabel.setText("ERR");
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // NAVIGATION
    // ─────────────────────────────────────────────────────────────────────────
    @FXML private void goToDashboard(ActionEvent e) { navigate(e, FXML_DASHBOARD); }
    @FXML private void goToInventory(ActionEvent e) { navigate(e, FXML_INVENTORY); }

    private void navigate(ActionEvent event, String path) {
        URL res = getClass().getResource(path);
        if (res == null) { showAlert("Cannot find: " + path); return; }
        try {
            Parent root = new FXMLLoader(res).load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showAlert("Failed to load: " + path + "\n" + e.getMessage());
            e.printStackTrace();
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // HELPERS
    // ─────────────────────────────────────────────────────────────────────────
    private CheckBox[] monthBoxes()   { return new CheckBox[]{cbJan,cbFeb,cbMar,cbApr,cbMay,cbJun,cbJul,cbAug,cbSep,cbOct,cbNov,cbDec}; }
    private CheckBox[] quarterBoxes() { return new CheckBox[]{cbQ1,cbQ2,cbQ3,cbQ4}; }
    private CheckBox[] yearBoxes()    { return new CheckBox[]{cb2024,cb2025,cb2026,cb2027,cb2028}; }

    private void showAlert(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}