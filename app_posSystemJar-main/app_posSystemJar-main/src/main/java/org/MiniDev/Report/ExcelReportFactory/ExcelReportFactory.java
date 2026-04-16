package org.MiniDev.Report.ExcelReportFactory;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import org.MiniDev.Report.ExcelReportFactory.CollectModel.DamagedDetailsModel;
import org.MiniDev.Report.ExcelReportFactory.CollectModel.ItemsSoldDetailsModel;
import org.MiniDev.Report.ExcelReportFactory.CollectModel.NetSalesDetailsModel;
import org.MiniDev.Report.ReportModel.OutOfStockModel;
import org.MiniDev.Report.ReportModel.TransactionCntModel;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

public class ExcelReportFactory {

    public static void exportNetSalesToExcel(List<NetSalesDetailsModel> netSalesLists, String filePath) {
        WritableWorkbook workbook = null;
        try {
            // Create a writable workbook at the given path
            workbook = Workbook.createWorkbook(new File(filePath));

            // Create a new sheet in the workbook
            WritableSheet sheet = workbook.createSheet("NetSales Data", 0);

            // Write column headers
            String[] columnNames = {"Sale Date", "Net Sales Amount", "Order Code", "Order Item Name",
                    "Order Qty", "Order SubTotal", "Order Price 1Qty", "Original SubTotal", "Order Notes"};
            for (int col = 0; col < columnNames.length; col++) {
                Label label = new Label(col, 0, columnNames[col]);
                sheet.addCell(label);
            }
            // Write the data rows
            for (int row = 0; row < netSalesLists.size(); row++) {
                NetSalesDetailsModel netSale = netSalesLists.get(row);

                sheet.addCell(new Label(0, row + 1, String.valueOf(netSale.getSaleDate())));
                sheet.addCell(new Label(1, row + 1, String.valueOf(netSale.getNetSales())));
                sheet.addCell(new Label(2, row + 1, String.valueOf(netSale.getOrderCode())));
                sheet.addCell(new Label(3, row + 1, netSale.getOrderItemName()));
                sheet.addCell(new Label(4, row + 1, String.valueOf(netSale.getOrderQty())));
                sheet.addCell(new Label(5, row + 1, String.valueOf(netSale.getOrderSubTotal())));
                sheet.addCell(new Label(6, row + 1, String.valueOf(netSale.getOrderPrice1Qty())));
                sheet.addCell(new Label(7, row + 1, String.valueOf(netSale.getOriginalSubTotal())));
                sheet.addCell(new Label(8, row + 1, String.valueOf(netSale.getOrderNotes())));
            }

            // Write the workbook to the file system
            workbook.write();
            System.out.println("Workbook written successfully!");

            JOptionPane.showMessageDialog(null, "Data successfully exported to Excel!", "Success", JOptionPane.INFORMATION_MESSAGE);

        } catch (IOException | WriteException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error exporting data to Excel: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            // Close the workbook
            if (workbook != null) {
                try {
                    workbook.close();
                } catch (IOException | WriteException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public static void exportItemsSoldToExcel(List<ItemsSoldDetailsModel> itemsSoldList, String filePath) {
        WritableWorkbook workbook = null;
        try {
            // Create a writable workbook at the given path
            workbook = Workbook.createWorkbook(new File(filePath));

            // Create a new sheet in the workbook
            WritableSheet sheet = workbook.createSheet("ItemsSold Data", 0);

            // Write column headers
            String[] columnNames = {"Sale Date", "Order Item Name", "Sold Qty", "Order SubTotal", "Original SubTotal"};
            for (int col = 0; col < columnNames.length; col++) {
                Label label = new Label(col, 0, columnNames[col]);
                sheet.addCell(label);
            }
            // Write the data rows
            for (int row = 0; row < itemsSoldList.size(); row++) {
                ItemsSoldDetailsModel itemsSold = itemsSoldList.get(row);

                sheet.addCell(new Label(0, row + 1, String.valueOf(itemsSold.getSaleDate())));
                sheet.addCell(new Label(1, row + 1, String.valueOf(itemsSold.getOrderItemName())));
                sheet.addCell(new Label(2, row + 1, String.valueOf(itemsSold.getOrderQtySold())));
                sheet.addCell(new Label(3, row + 1, String.valueOf(itemsSold.getOrderSubTotal())));
                sheet.addCell(new Label(4, row + 1, String.valueOf(itemsSold.getOriginalSubTotal())));

            }

            // Write the workbook to the file system
            workbook.write();
            System.out.println("Workbook written successfully!");

            JOptionPane.showMessageDialog(null, "Data successfully exported to Excel!", "Success", JOptionPane.INFORMATION_MESSAGE);

        } catch (IOException | WriteException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error exporting data to Excel: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            // Close the workbook
            if (workbook != null) {
                try {
                    workbook.close();
                } catch (IOException | WriteException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public static void exportDamagedListToExcel(List<DamagedDetailsModel> damagedDetailsModelList, String filePath) {
        WritableWorkbook workbook = null;
        try {
            // Create a writable workbook at the given path
            workbook = Workbook.createWorkbook(new File(filePath));

            // Create a new sheet in the workbook
            WritableSheet sheet = workbook.createSheet("ItemsSold Data", 0);

            // Write column headers
            String[] columnNames = {"Report Date", "Expense Code", "Expense Code Name", "AssignedTo Employee", "Last Update Date", "Entry Employee", "Damaged Amount"};
            for (int col = 0; col < columnNames.length; col++) {
                Label label = new Label(col, 0, columnNames[col]);
                sheet.addCell(label);
            }
            // Write the data rows
            for (int row = 0; row < damagedDetailsModelList.size(); row++) {
                DamagedDetailsModel damaged = damagedDetailsModelList.get(row);


                sheet.addCell(new Label(0, row + 1, String.valueOf(damaged.getReportDate())));
                sheet.addCell(new Label(1, row + 1, String.valueOf(damaged.getExpCode())));
                sheet.addCell(new Label(2, row + 1, String.valueOf(damaged.getExpCodeName())));
                sheet.addCell(new Label(3, row + 1, String.valueOf(damaged.getAssignedToEmp())));
                sheet.addCell(new Label(4, row + 1, String.valueOf(damaged.getLastUpdateDate())));
                sheet.addCell(new Label(5, row + 1, String.valueOf(damaged.getEntryEmp())));
                sheet.addCell(new Label(6, row + 1, String.valueOf(damaged.getDamagedAmount())));

            }

            // Write the workbook to the file system
            workbook.write();
            System.out.println("Workbook written successfully!");

            JOptionPane.showMessageDialog(null, "Data successfully exported to Excel!", "Success", JOptionPane.INFORMATION_MESSAGE);

        } catch (IOException | WriteException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error exporting data to Excel: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            // Close the workbook
            if (workbook != null) {
                try {
                    workbook.close();
                } catch (IOException | WriteException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public static void exportTransactionsCntToExcel(List<TransactionCntModel> trnsactionsCntModelList, String filePath) {
        WritableWorkbook workbook = null;
        try {
            // Create a writable workbook at the given path
            workbook = Workbook.createWorkbook(new File(filePath));

            // Create a new sheet in the workbook
            WritableSheet sheet = workbook.createSheet("TransactionCount Data", 0);

            // Write column headers
            String[] columnNames = {"Transaction Date", "Transaction Count"};
            for (int col = 0; col < columnNames.length; col++) {
                Label label = new Label(col, 0, columnNames[col]);
                sheet.addCell(label);
            }
            // Write the data rows
            for (int row = 0; row < trnsactionsCntModelList.size(); row++) {
                TransactionCntModel trnCnt = trnsactionsCntModelList.get(row);

                sheet.addCell(new Label(0, row + 1, String.valueOf(trnCnt.getTrnDate())));
                sheet.addCell(new Label(1, row + 1, String.valueOf(trnCnt.getTrnCnt())));
            }

            // Write the workbook to the file system
            workbook.write();
            System.out.println("Workbook written successfully!");

            JOptionPane.showMessageDialog(null, "Data successfully exported to Excel!", "Success", JOptionPane.INFORMATION_MESSAGE);

        } catch (IOException | WriteException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error exporting data to Excel: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            // Close the workbook
            if (workbook != null) {
                try {
                    workbook.close();
                } catch (IOException | WriteException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public static void exportOutOfStockToExcel(List<OutOfStockModel> outOfStockModelList, String filePath) {
        WritableWorkbook workbook = null;
        try {
            // Create a writable workbook at the given path
            workbook = Workbook.createWorkbook(new File(filePath));

            // Create a new sheet in the workbook
            WritableSheet sheet = workbook.createSheet("OutOfStock Data", 0);

            // Write column headers
            String[] columnNames = {"OutOfStock Date", "OutOfStock Item"};
            for (int col = 0; col < columnNames.length; col++) {
                Label label = new Label(col, 0, columnNames[col]);
                sheet.addCell(label);
            }
            // Write the data rows
            for (int row = 0; row < outOfStockModelList.size(); row++) {
                OutOfStockModel outOfStockModel = outOfStockModelList.get(row);

                sheet.addCell(new Label(0, row + 1, String.valueOf(outOfStockModel.getOutDate())));
                sheet.addCell(new Label(1, row + 1, String.valueOf(outOfStockModel.getOutItemName())));
            }

            // Write the workbook to the file system
            workbook.write();
            System.out.println("Workbook written successfully!");

            JOptionPane.showMessageDialog(null, "Data successfully exported to Excel!", "Success", JOptionPane.INFORMATION_MESSAGE);

        } catch (IOException | WriteException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error exporting data to Excel: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            // Close the workbook
            if (workbook != null) {
                try {
                    workbook.close();
                } catch (IOException | WriteException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
