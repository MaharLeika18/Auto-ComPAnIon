package org.MiniDev.Backoffice.EmployeeManagement.EmployeeListManagement.EmpTableActionPages;

import DBConnection.DBConnection;
import NetworkUtils.NetworkUtils;
import UI.*;
import org.MiniDev.Backoffice.EmployeeManagement.EmployeeListManagement.EmpMainPage;
import org.MiniDev.Backoffice.EmployeeManagement.EmployeeListManagement.EmpTableActionPages.EmpPagesInterface.EmpPage;
import org.MiniDev.Backoffice.EmployeeManagement.EmployeeListManagement.EmpTableActionPages.EmpUIComponents.EmpUIFactory;
import org.MiniDev.Backoffice.EmployeeManagement.EmployeeManagementPage;
import org.MiniDev.Login.PasswordUtils;
import raven.datetime.component.date.DatePicker;

import javax.swing.*;
import java.awt.*;
import java.sql.CallableStatement;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static UI.UserFinalSettingsVar.*;

public class EmpEditPage extends Component implements EmpPage {

    protected static RoundedPanel empEditEntryPanel;

    protected static RoundedTextFieldV2 hiddenIDField = new RoundedTextFieldV2(10, 1, COLOR_LINE_COLOR);

    protected static RoundedTextFieldV2 nameField = new RoundedTextFieldV2(10, 1, COLOR_LINE_COLOR);
    protected static RoundedPasswordField empTellerPasswordField = new RoundedPasswordField(10, 1, COLOR_LINE_COLOR);
    protected static RoundedTextFieldV2 fatherNameField = new RoundedTextFieldV2(10, 1, COLOR_LINE_COLOR);
    protected static RoundedTextFieldV2 mobileField = new RoundedTextFieldV2(10, 1, COLOR_LINE_COLOR);
    protected static RoundedTextFieldV2 addressField = new RoundedTextFieldV2(10, 1, COLOR_LINE_COLOR);
    protected static RoundedTextFieldV2 nidField = new RoundedTextFieldV2(10, 1, COLOR_LINE_COLOR);
    protected static RoundedTextFieldV2 salaryField = new RoundedTextFieldV2(10, 1, COLOR_LINE_COLOR);

    protected static DatePicker birthDatePicker = new DatePicker();
    protected static JFormattedTextField birthdateEditor = new JFormattedTextField();
    protected static DatePicker hiredDatePicker = new DatePicker();
    protected static JFormattedTextField hiredDateEditor = new JFormattedTextField();

    protected static List<String> jobTitleOptions = new ArrayList<>(Arrays.asList("Manager", "Officer", "Waiter", "Chief", "Cashier", "General Worker", "Owner", "Sales", "Security", "Store Keeper"));
    protected static List<String> genderOptions = new ArrayList<>(Arrays.asList("Male", "Female"));
    protected static List<String> maritalStatusOptions = new ArrayList<>(Arrays.asList("Single", "Married", "Others"));

    protected static RoundedComboBox<String> jobTitleCombo = new RoundedComboBox<>(10, 1, COLOR_LINE_COLOR);
    protected static RoundedComboBox<String> genderCombo = new RoundedComboBox<>(10, 1, COLOR_LINE_COLOR);
    protected static RoundedComboBox<String> maritalCombo = new RoundedComboBox<>(10, 1, COLOR_LINE_COLOR);

    protected static EditableFoodImage addNewImageAvatar = new EditableFoodImage();
    protected static IconTextButton saveChangesEmpButt = new IconTextButton("Save Changes", null, 10, COLOR_BUTTON_BLUE, 0);

    public EmpEditPage() {
    }

    @Override
    public RoundedPanel createEmpPage() {
        createEmpEditPage();
        return null;
    }

    public RoundedPanel createEmpEditPage() {

        empEditEntryPanel = new RoundedPanel(10);
        empEditEntryPanel.setLayout(new BorderLayout());
        empEditEntryPanel.setOpaque(false);
        empEditEntryPanel.add(getUpperPanel(), BorderLayout.NORTH);
        empEditEntryPanel.add(getBottomPanel(), BorderLayout.SOUTH);
        empEditEntryPanel.add(getUploadPanel(), BorderLayout.WEST);
        empEditEntryPanel.add(getTextFieldsHoldingPanel(), BorderLayout.CENTER);

        return empEditEntryPanel;
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

        JLabel pageLabel = new JLabel("Edit Employee");
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

        bottom.add(EmpUIFactory.getCreateNewEmpButton(saveChangesEmpButt, COLOR_BUTTON_BLUE, getEditEmpInputData()));

        return bottom;
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

        center.add(EmpUIFactory.getFieldPanel("Name", nameField, "Enter Employee Name", false, true));
        center.add(EmpUIFactory.getPasswordField("Password", empTellerPasswordField, "Enter Password", false, false));
        center.add(EmpUIFactory.getFieldPanel("Father Name", fatherNameField, "Enter Employee's Father Name", false, false));
        center.add(EmpUIFactory.getFieldPanel("Mobile Number", mobileField, "Enter Mobile Number", false, false));
        center.add(EmpUIFactory.getFieldPanel("Address", addressField, "Enter Address", false, false));
        center.add(EmpUIFactory.getFieldPanel("National ID", nidField, "Enter NRC", false, false));
        center.add(EmpUIFactory.getFieldPanel("Salary", salaryField, "Enter Salary", true, false));

        center.add(EmpUIFactory.getDateFieldPanel("Date of Birth", birthDatePicker, birthdateEditor, false));
        center.add(EmpUIFactory.getDateFieldPanel("Hired Date", hiredDatePicker, hiredDateEditor, false));

        center.add(EmpUIFactory.getComboFieldPanel("Job Title", jobTitleCombo, jobTitleOptions, false));
        center.add(EmpUIFactory.getComboFieldPanel("Gender", genderCombo, genderOptions, false));
        center.add(EmpUIFactory.getComboFieldPanel("Marital Status", maritalCombo, maritalStatusOptions, false));

        return center;
    }

    public static void setDataInFields(int id, Icon empIcon, String nameValue, String rawPasswordValue, String fatherValue, String telNoValue,
                                       String addressValue, String nrcValue, int salaryValue, LocalDate birthDateValue,
                                       LocalDate hiredDateValue, String jobTitleValue, String genderValue, String maritalValue) {

        SwingUtilities.invokeLater(() -> {
            hiddenIDField.setText(String.valueOf(id));
            addNewImageAvatar.setImage(empIcon);
            nameField.setText(nameValue);
            empTellerPasswordField.setText(rawPasswordValue);
            fatherNameField.setText(fatherValue);
            mobileField.setText(telNoValue);
            addressField.setText(addressValue);
            nidField.setText(nrcValue);
            salaryField.setText(String.valueOf(salaryValue));
            birthDatePicker.setSelectedDate(birthDateValue);
            hiredDatePicker.setSelectedDate(hiredDateValue);
            jobTitleCombo.setSelectedItem(jobTitleValue);
            genderCombo.setSelectedItem(genderValue);
            maritalCombo.setSelectedItem(maritalValue);
        });
    }

    private Runnable getEditEmpInputData() {
        return () -> {
            // Immediately show EMP-MN page
            EmployeeManagementPage.employeeMenuCardLayout.show(
                    EmployeeManagementPage.menuEmployeeCardHoldingPanel,
                    "EMP-MN"
            );

            // Set waiting cursor
            final Cursor waitCursor = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
            EmployeeManagementPage.menuEmployeeCardHoldingPanel.setCursor(waitCursor);

            // Single SwingWorker for all background tasks
            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    // Validate first
                    if (!areInputFieldsValid()) {
                        return null;
                    }

                    // Database operation
                    try {
                        dbBatchProcessUpdateEmp(
                                getProfilePics(),
                                nameField.getText().trim(),
                                fatherNameField.getText().trim(),
                                mobileField.getText().trim(),
                                addressField.getText().trim(),
                                nidField.getText().trim(),
                                salaryField.getText().trim(),
                                birthDatePicker.getSelectedDate(),
                                hiredDatePicker.getSelectedDate(),
                                jobTitleCombo.getSelectedItem(),
                                genderCombo.getSelectedItem(),
                                maritalCombo.getSelectedItem(),
                                Integer.parseInt(hiddenIDField.getText())
                        );
                    } catch (NumberFormatException nfe) {
                        throw new Exception("Invalid employee ID format", nfe);
                    } catch (Exception ex) {
                        throw new Exception("Database update failed: " + ex.getMessage(), ex);
                    }

                    // Background table data refresh
                    EmpMainPage.refreshTableAfterEmpUpdater();
                    return null;
                }

                @Override
                protected void done() {
                    try {
                        get(); // Check for exceptions

                        // Update UI on EDT
                        SwingUtilities.invokeLater(() -> {

                            // Show success notification
                            JOptionPane.showMessageDialog(
                                    empEditEntryPanel,
                                    "Employee updated successfully!",
                                    "Success",
                                    JOptionPane.INFORMATION_MESSAGE
                            );
                        });
                    } catch (Exception ex) {
                        SwingUtilities.invokeLater(() ->
                                JOptionPane.showMessageDialog(
                                        empEditEntryPanel,
                                        "Error: " + ex.getCause().getMessage(),
                                        "Update Error",
                                        JOptionPane.ERROR_MESSAGE
                                )
                        );
                    } finally {
                        // Always restore cursor
                        SwingUtilities.invokeLater(() ->
                                EmployeeManagementPage.menuEmployeeCardHoldingPanel.setCursor(
                                        Cursor.getDefaultCursor()
                                )
                        );
                    }
                }
            }.execute();
        };
    }

    private byte[] getProfilePics() {
        if (addNewImageAvatar.getImageBytes() == null) {
            return null;
        } else {
            return addNewImageAvatar.getImageBytes();
        }
    }


    private boolean areInputFieldsValid() {
        // Check if any of the required fields are empty or invalid
        if (nameField.getText().trim().isEmpty()) {
            showError("Name must be filled.");
            return false;
        }
        if (fatherNameField.getText().trim().isEmpty()) {
            showError("Father's name must be filled.");
            return false;
        }
        if (mobileField.getText().trim().isEmpty()) {
            showError("Mobile number must be filled.");
            return false;
        }
        if (addressField.getText().trim().isEmpty()) {
            showError("Address must be filled.");
            return false;
        }
        if (nidField.getText().trim().isEmpty()) {
            showError("NID must be filled.");
            return false;
        }
        if (salaryField.getText().trim().isEmpty()) {
            showError("Salary must be filled.");
            return false;
        }
        if (birthDatePicker.getSelectedDate() == null) {
            showError("Birth date must be selected.");
            return false;
        }
        if (hiredDatePicker.getSelectedDate() == null) {
            showError("Hired date must be selected.");
            return false;
        }
        if (jobTitleCombo.getSelectedItem() == null) {
            showError("Job title must be selected.");
            return false;
        }
        if (genderCombo.getSelectedItem() == null) {
            showError("Gender must be selected.");
            return false;
        }
        if (maritalCombo.getSelectedItem() == null) {
            showError("Marital status must be selected.");
            return false;
        }
        return true; // All fields are valid
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(EmpNewPage.empNewEntryPanel, message, "Validation Error", JOptionPane.ERROR_MESSAGE);
    }


    public void dbBatchProcessUpdateEmp(
            byte[] p_EmpImg,
            String p_EmpName,
            String p_EmpFatherName,
            String p_MobileNo,
            String p_Address,
            String p_NRC,
            String p_Salary,
            LocalDate p_BirthDate,
            LocalDate p_HiredDate,
            Object p_Position,
            Object p_Gender,
            Object p_MaritalStatus,
            int empID) {
        String sql = "{CALL sp_UpdateEmp(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";


        try (java.sql.Connection conn = DBConnection.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setBytes(1, p_EmpImg);
            stmt.setString(2, p_EmpName);
            stmt.setString(3, p_EmpFatherName);
            stmt.setString(4, p_MobileNo);
            stmt.setString(5, p_Address);
            stmt.setString(6, p_NRC);
            stmt.setInt(7, trimAndParseNumber(p_Salary) != null ? trimAndParseNumber(p_Salary) : 0);
            stmt.setDate(8, p_BirthDate != null ? Date.valueOf(p_BirthDate) : null);
            stmt.setDate(9, p_HiredDate != null ? Date.valueOf(p_HiredDate) : null);
            stmt.setString(10, p_Position != null ? p_Position.toString() : "Unknown");
            stmt.setString(11, p_Gender != null ? p_Gender.toString() : "Unknown");
            stmt.setString(12, p_MaritalStatus != null ? p_MaritalStatus.toString() : "Unknown");
            stmt.setString(13, NetworkUtils.getLocalMachineName());
            stmt.setString(14, NetworkUtils.getCurrentIPAddress());
            stmt.setInt(15, empID);

            // Execute the stored procedure
            stmt.execute();

            System.out.println(empID);


            JOptionPane.showMessageDialog(empEditEntryPanel,
                    "Employee data has been successfully updated to the database.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException e) {
            System.err.println("SQL Error Code: " + e.getErrorCode());
            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Error Message: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to execute stored procedure. See logs for details.");
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException("Unexpected error occurred while processing update employee data.");
        }


    }

    public Integer trimAndParseNumber(String formattedNumber) {
        if (formattedNumber == null || formattedNumber.isEmpty()) {
            return null; // or throw an exception based on your requirements
        }

        // Remove commas
        String trimmedNumber = formattedNumber.replace(",", "");

        try {
            return Integer.parseInt(trimmedNumber);
        } catch (NumberFormatException e) {
            return null; // or throw an exception based on your requirements
        }
    }


}
