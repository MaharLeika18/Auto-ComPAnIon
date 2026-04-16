package org.MiniDev.Backoffice.EmployeeManagement.EmployeeListManagement.EmpTableActionPages;

import UI.*;
import jxl.Workbook;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import org.MiniDev.Backoffice.EmployeeManagement.EmployeeListManagement.EmpMainPage;
import org.MiniDev.Backoffice.EmployeeManagement.EmployeeListManagement.EmpTableActionPages.EmpUIComponents.EmpUIFactory;
import org.MiniDev.OOP.EmployeeHistoryLists;
import org.MiniDev.Report.ExcelReportFactory.ExcelReportFactory;
import org.MiniDev.Report.ReportModel.OutOfStockModel;
import org.MiniDev.Report.ReportService.OutOfStockService;
import raven.datetime.component.date.DatePicker;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static UI.UserFinalSettingsVar.*;

public class EmpViewPage extends Component {

    protected static RoundedPanel empViewPanel;

    protected static RoundedTextFieldV2 nameField = new RoundedTextFieldV2(10, 1, COLOR_LINE_COLOR);
    protected static RoundedTextFieldV2 empIDField = new RoundedTextFieldV2(10, 1, COLOR_LINE_COLOR);
    protected static RoundedTextFieldV2 fatherNameField = new RoundedTextFieldV2(10, 1, COLOR_LINE_COLOR);
    protected static RoundedTextFieldV2 mobileField = new RoundedTextFieldV2(10, 1, COLOR_LINE_COLOR);
    protected static RoundedTextFieldV2 addressField = new RoundedTextFieldV2(10, 1, COLOR_LINE_COLOR);
    protected static RoundedTextFieldV2 nidField = new RoundedTextFieldV2(10, 1, COLOR_LINE_COLOR);
    protected static RoundedTextFieldV2 salaryField = new RoundedTextFieldV2(10, 1, COLOR_LINE_COLOR);

    protected static RoundedTextFieldV2 birthDatePicker = new RoundedTextFieldV2(10, 1, COLOR_LINE_COLOR);

    protected static RoundedTextFieldV2 hiredDatePicker = new RoundedTextFieldV2(10, 1, COLOR_LINE_COLOR);


    protected static List<String> jobTitleOptions = new ArrayList<>(Arrays.asList("Manager", "Officer", "Waiter", "Chief", "Cashier", "General Worker", "Owner", "Sales", "Security", "Store Keeper"));
    protected static List<String> genderOptions = new ArrayList<>(Arrays.asList("Male", "Female"));
    protected static List<String> maritalStatusOptions = new ArrayList<>(Arrays.asList("Single", "Married", "Others"));

    protected static RoundedTextFieldV2 jobTitleCombo = new RoundedTextFieldV2(10, 1, COLOR_LINE_COLOR);
    protected static RoundedTextFieldV2 genderCombo = new RoundedTextFieldV2(10, 1, COLOR_LINE_COLOR);
    protected static RoundedTextFieldV2 maritalCombo = new RoundedTextFieldV2(10, 1, COLOR_LINE_COLOR);

    protected static EditableFoodImage addNewImageAvatar = new EditableFoodImage();

    public EmpViewPage() {
    }

    public RoundedPanel createEmpViewPage() {
        empViewPanel = new RoundedPanel(10);
        empViewPanel.setLayout(new BorderLayout());
        empViewPanel.setOpaque(false);
        empViewPanel.add(getUpperPanel(), BorderLayout.NORTH);
        empViewPanel.add(getBottomPanel(), BorderLayout.SOUTH);
        empViewPanel.add(getUploadPanel(), BorderLayout.WEST);
        empViewPanel.add(getTextFieldsHoldingPanel(), BorderLayout.CENTER);

        return empViewPanel;
    }

    private JPanel getUpperPanel() {
        JPanel upper = new JPanel(new BorderLayout());
        upper.setOpaque(true);
        upper.setBackground(COLOR_WHITE);
        upper.setPreferredSize(new Dimension(500, 40));
        upper.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_LINE_COLOR),
                BorderFactory.createEmptyBorder(0, 10, 3, 10)
        ));

        JLabel pageLabel = new JLabel("View Employee");
        pageLabel.setFont(new Font("Noto Sans Myanmar", Font.BOLD, 16));
        pageLabel.setForeground(COLOR_FONT_GRAY);

        upper.add(pageLabel, BorderLayout.WEST);
        upper.add(EmpUIFactory.getCloseThisWindowButt(), BorderLayout.EAST);

        return upper;
    }


    private JPanel getBottomPanel() {
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setOpaque(true);
        bottom.setBackground(COLOR_WHITE);
        bottom.setPreferredSize(new Dimension(500, 50));

        bottom.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(1, 0, 0, 0, COLOR_LINE_COLOR), // Top matte border
                        BorderFactory.createEmptyBorder(3, 10, 1, 5) // Empty border inside
                )
        );

        IconTextButton downloadButton = EmpUIFactory.getExcelDownloadButton("Download Profile", "/ExcelIcon.svg", 18, Color.decode("#65a30d"), COLOR_WHITE, 140, 40);
        downloadButton.addActionListener(e -> {
            doDownloadEmployeeExcel(Integer.parseInt(empIDField.getText()));
        });


        bottom.add(downloadButton);

        return bottom;
    }

    private void doDownloadEmployeeExcel(int empID) {
        List<EmployeeHistoryLists> resultList = new ArrayList<>();
        EmpMainPage.empArrayListsToUpdate.stream()
                .filter(p -> p.getEmployeeId() == empID)
                .findFirst()
                .ifPresent(resultList::add);

        String filePath = empID+"-EmployeeData.xls";  // Define the file path for the exported file
        employeeExcelDataGenerate(resultList, filePath);  // Export data to Excel

        // Open the exported Excel file after creation
        File excelFile = new File(filePath);
        if (excelFile.exists()) {
            try {
                // Open the file using the default application associated with Excel files
                Desktop.getDesktop().open(excelFile);
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Unable to open the exported Excel file.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null, "The exported file was not found.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void employeeExcelDataGenerate(List<EmployeeHistoryLists> emp, String filePath) {
        WritableWorkbook workbook = null;
        try {
            // Create a writable workbook at the given path
            workbook = Workbook.createWorkbook(new File(filePath));

            // Create a new sheet in the workbook
            WritableSheet sheet = workbook.createSheet("Employee Data", 0);

            // Write column headers
            String[] columnNames = {" ID"," Name"," Address","TelNo","BirthDate","FixedSalary","Department/Position","HiredDate","MaritalStatus","NRC","FatherName"};
            for (int col = 0; col < columnNames.length; col++) {
                jxl.write.Label label = new jxl.write.Label(col, 0, columnNames[col]);
                sheet.addCell(label);
            }
            // Write the data rows
            for (int row = 0; row < emp.size(); row++) {
                EmployeeHistoryLists result = emp.get(row);

                sheet.addCell(new jxl.write.Label(0, row + 1, String.valueOf(result.getEmployeeId())));
                sheet.addCell(new jxl.write.Label(1, row + 1, String.valueOf(result.getEmployeeName())));
                sheet.addCell(new jxl.write.Label(2,row + 1, String.valueOf(result.getHomeAddress())));
                sheet.addCell(new jxl.write.Label(3,row + 1, String.valueOf(result.getEmployeeTelNo())));
                sheet.addCell(new jxl.write.Label(4,row + 1, String.valueOf(result.getBirthDate())));
                sheet.addCell(new jxl.write.Label(5,row + 1, String.valueOf(result.getCurrentFixedSalaryAmount())));
                sheet.addCell(new jxl.write.Label(6,row + 1, String.valueOf(result.getDepartmentName())));
                sheet.addCell(new jxl.write.Label(7,row + 1, String.valueOf(result.getHireDate())));
                sheet.addCell(new jxl.write.Label(8,row + 1, String.valueOf(result.getMaritalStatus())));
                sheet.addCell(new jxl.write.Label(9,row + 1, String.valueOf(result.getNrc())));
                sheet.addCell(new jxl.write.Label(10,row + 1, String.valueOf(result.getFatherName())));


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


    private JPanel getUploadPanel() {
        JPanel upload = new JPanel(new BorderLayout());
        upload.setOpaque(true);
        upload.setBackground(COLOR_WHITE);
        upload.setPreferredSize(new Dimension(220, 600));
        upload.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        upload.add(EmpUIFactory.getAddNewImageAvatar(addNewImageAvatar), BorderLayout.NORTH);

        return upload;
    }

    private JPanel getTextFieldsHoldingPanel() {
        JPanel center = new JPanel(new GridLayout(6, 2, 25, 3));
        center.setOpaque(true);
        center.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        center.setBackground(COLOR_WHITE);

        center.add(EmpUIFactory.getFieldPanel("Name", nameField, "eg. Maung Maung", false, true));
        center.add(EmpUIFactory.getFieldPanel("Employee ID", empIDField, "eg. 001", false, true));
        center.add(EmpUIFactory.getFieldPanel("Father Name", fatherNameField, "eg. U Maung", false, true));
        center.add(EmpUIFactory.getFieldPanel("Mobile Number", mobileField, "eg. 09777555500", false, true));
        center.add(EmpUIFactory.getFieldPanel("Address", addressField, "eg. AungMyayTharZan Township, Mandalay", false, true));
        center.add(EmpUIFactory.getFieldPanel("National ID", nidField, "eg. 9/ahmaza(N)00001", false, true));
        center.add(EmpUIFactory.getFieldPanel("Salary", salaryField, "eg. Mandalay", true, true));

        center.add(EmpUIFactory.getFieldPanel("Date of Birth", birthDatePicker, "", false, true));
        center.add(EmpUIFactory.getFieldPanel("Hired Date", hiredDatePicker, "", false, true));

        center.add(EmpUIFactory.getFieldPanel("Job Title", jobTitleCombo, "", false, true));
        center.add(EmpUIFactory.getFieldPanel("Gender", genderCombo, "", false, true));
        center.add(EmpUIFactory.getFieldPanel("Marital Status", maritalCombo, "", false, true));

        return center;
    }

    public static void setDataInFields(Icon empIcon, String nameValue, int empIDValue, String fatherValue, String telNoValue,
                                       String addressValue, String nrcValue, int salaryValue, LocalDate birthDateValue,
                                       LocalDate hiredDateValue, String jobTitleValue, String genderValue, String maritalValue) {


        SwingUtilities.invokeLater(() -> {
            addNewImageAvatar.setImage(empIcon);
            nameField.setText(nameValue);
            empIDField.setText(String.valueOf(empIDValue));
            fatherNameField.setText(fatherValue);
            mobileField.setText(telNoValue);
            addressField.setText(addressValue);
            nidField.setText(nrcValue);
            salaryField.setText(String.valueOf(salaryValue));
            birthDatePicker.setText(getFormattedDate(birthDateValue));
            hiredDatePicker.setText(getFormattedDate(hiredDateValue));
            jobTitleCombo.setText(jobTitleValue);
            genderCombo.setText(genderValue);
            maritalCombo.setText(maritalValue);
        });
    }

    public static String getFormattedDate(LocalDate date) {
        if (date != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            return date.format(formatter);
        }
        return "";
    }
}
