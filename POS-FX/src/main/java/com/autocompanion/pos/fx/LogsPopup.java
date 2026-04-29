package com.autocompanion.pos.fx;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.MiniDev.DBConnection.DBConnection;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

public class LogsPopup {

    private static final String SQL_ORDERS = 
        "SELECT r.receipt_num, r.total_amount, r.payment_method, r.status, r.transaction_date " +
        "FROM transaction_log r ORDER BY r.transaction_date DESC";

    public static void show(Stage owner) {
        Stage stage = new Stage();
        stage.initOwner(owner);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Transaction Logs");
        stage.setWidth(900);
        stage.setHeight(600);

        VBox root = new VBox(10);
        root.setPadding(new Insets(15));

        Label title = new Label("Transaction Orders");
        title.setFont(new Font("Montserrat", 18));
        title.setStyle("-fx-font-weight: bold;");
        root.getChildren().add(title);

        TableView<OrderLog> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<OrderLog, String> colReceipt = new TableColumn<>("Receipt No");
        colReceipt.setCellValueFactory(new PropertyValueFactory<>("receiptNo"));
        colReceipt.setPrefWidth(150);

        TableColumn<OrderLog, Double> colTotal = new TableColumn<>("Total");
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        colTotal.setCellFactory(c -> new TableCell<OrderLog, Double>() {
            @Override protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(item == null || empty ? null : String.format("₱%.2f", item));
            }
        });
        colTotal.setPrefWidth(100);

        TableColumn<OrderLog, OrderLog.Payment> colPayment = new TableColumn<>("Payment");
        colPayment.setCellValueFactory(new PropertyValueFactory<>("payment"));
        colPayment.setPrefWidth(100);

        TableColumn<OrderLog, OrderLog.Status> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colStatus.setCellFactory(c -> new TableCell<OrderLog, OrderLog.Status>() {
            @Override protected void updateItem(OrderLog.Status item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item.name());
                    String color = item == OrderLog.Status.APPROVED ? "#4CAF50" :
                                   item == OrderLog.Status.REJECTED ? "#F44336" : "#FF9800";
                    setStyle("-fx-text-fill: " + color + "; -fx-font-weight: bold;");
                }
            }
        });
        colStatus.setPrefWidth(100);

        TableColumn<OrderLog, Timestamp> colDate = new TableColumn<>("Date");
        colDate.setCellValueFactory(new PropertyValueFactory<>("createdDate"));
        colDate.setCellFactory(c -> new TableCell<OrderLog, Timestamp>() {
            @Override protected void updateItem(Timestamp item, boolean empty) {
                super.updateItem(item, empty);
                setText(item == null || empty ? null : item.toString());
            }
        });
        colDate.setPrefWidth(180);

        TableColumn<OrderLog, Void> colAction = new TableColumn<>("Action");
        colAction.setCellFactory(new Callback<TableColumn<OrderLog, Void>, TableCell<OrderLog, Void>>() {
            @Override public TableCell<OrderLog, Void> call(TableColumn<OrderLog, Void> col) {
                return new TableCell<OrderLog, Void>() {
                    private final Button btn = new Button("Approve / Reject");
                    {
                        btn.setOnAction(e -> {
                            OrderLog log = getTableView().getItems().get(getIndex());
                            showApprovalDialog(log);
                        });
                    }
                    @Override protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        setGraphic(empty ? null : btn);
                    }
                };
            }
        });
        colAction.setPrefWidth(150);

        table.getColumns().addAll(colReceipt, colTotal, colPayment, colStatus, colDate, colAction);

        ObservableList<OrderLog> orders = loadOrders();
        table.setItems(orders);

        ScrollPane scroll = new ScrollPane(table);
        scroll.setFitToWidth(true);
        scroll.setFitToHeight(true);
        root.getChildren().add(scroll);

        Button closeBtn = new Button("Close");
        closeBtn.setOnAction(e -> stage.close());
        HBox btnBox = new HBox(closeBtn);
        btnBox.setAlignment(Pos.CENTER_RIGHT);
        root.getChildren().add(btnBox);

        stage.setScene(new Scene(root));
        stage.showAndWait();
    }

    private static ObservableList<OrderLog> loadOrders() {
        ObservableList<OrderLog> list = FXCollections.observableArrayList();
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SQL_ORDERS)) {
            while (rs.next()) {
                String receipt = String.valueOf(rs.getInt("receipt_num"));
                double total = rs.getDouble("total_amount");
                String paymentStr = rs.getString("payment_method");
                String statusStr = rs.getString("status");
                Timestamp created = rs.getTimestamp("transaction_date");

                OrderLog.Payment payment = "E-WALLET".equalsIgnoreCase(paymentStr) ? OrderLog.Payment.GCASH :
                                           "BANK".equalsIgnoreCase(paymentStr) ? OrderLog.Payment.BANK :
                                           OrderLog.Payment.CASH;
                OrderLog.Status status = "CONFIRMED".equalsIgnoreCase(statusStr) ? OrderLog.Status.APPROVED :
                                         "CANCELLED".equalsIgnoreCase(statusStr) ? OrderLog.Status.REJECTED :
                                         OrderLog.Status.PENDING;

                list.add(new OrderLog(receipt, new ArrayList<>(), total, payment, status, created));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to load orders:\n" + e.getMessage())
                .showAndWait();
        }
        return list;
    }

    private static void showApprovalDialog(OrderLog log) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Approve / Reject Order");
        alert.setHeaderText("Receipt: " + log.getReceiptNo() + "\nTotal: ₱" + String.format("%.2f", log.getTotal()));
        alert.setContentText("Choose an action:");

        ButtonType btnApprove = new ButtonType("Approve");
        ButtonType btnReject = new ButtonType("Reject");
        ButtonType btnCancel = ButtonType.CANCEL;

        alert.getButtonTypes().setAll(btnApprove, btnReject, btnCancel);

        alert.showAndWait().ifPresent(result -> {
            if (result == btnApprove) {
                updateOrderStatus(log.getReceiptNo(), "APPROVED");
                log.setStatus(OrderLog.Status.APPROVED);
            } else if (result == btnReject) {
                updateOrderStatus(log.getReceiptNo(), "REJECTED");
                log.setStatus(OrderLog.Status.REJECTED);
            }
        });
    }

    private static void updateOrderStatus(String receiptNo, String status) {
        // Convert our status to database status
        String dbStatus;
        try {
            int receiptNum = Integer.parseInt(receiptNo);
            if ("APPROVED".equalsIgnoreCase(status)) {
                dbStatus = "CONFIRMED";
            } else if ("REJECTED".equalsIgnoreCase(status)) {
                dbStatus = "CANCELLED";
            } else {
                dbStatus = "PENDING";
            }
            
            String sql = "UPDATE transaction_log SET status = ? WHERE receipt_num = ?";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, dbStatus);
                ps.setInt(2, receiptNum);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to update status:\n" + e.getMessage())
                .showAndWait();
        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "Invalid receipt number: " + receiptNo)
                .showAndWait();
        }
    }
}