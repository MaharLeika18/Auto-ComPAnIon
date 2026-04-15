package org.MiniDev.Backoffice.EmployeeManagement.EmployeeListManagement.EmpTableActionPages;

import DBConnection.DBConnection;
import NetworkUtils.NetworkUtils;
import UI.*;
import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
import org.MiniDev.Backoffice.EmployeeManagement.EmployeeListManagement.EmpMainPage;
import org.MiniDev.Backoffice.EmployeeManagement.EmployeeListManagement.EmpTableActionPages.EmpPagesInterface.EmpPage;
import org.MiniDev.Backoffice.EmployeeManagement.EmployeeListManagement.EmpTableActionPages.EmpUIComponents.EmpUIFactory;
import org.MiniDev.Login.PasswordUtils;
import org.mariadb.jdbc.Connection;
import raven.datetime.component.date.DatePicker;

import javax.swing.*;
import java.awt.*;
import java.sql.CallableStatement;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.mariadb.jdbc.Connection; // Import MariaDB-specific Connection

import static UI.UserFinalSettingsVar.*;

public class EmpNewPage extends Component implements EmpPage {

    public static RoundedPanel empNewEntryPanel;

    protected static RoundedTextFieldV2 nameField = new RoundedTextFieldV2(10, 1, COLOR_LINE_COLOR);
    protected static RoundedPasswordField empTellerPasswordField = new RoundedPasswordField(10, 1, COLOR_LINE_COLOR);
    protected static RoundedTextFieldV2 fatherNameField = new RoundedTextFieldV2(10, 1, COLOR_LINE_COLOR);
    protected static RoundedTextFieldV2 mobileField = new RoundedTextFieldV2(10, 1, COLOR_LINE_COLOR);
    protected static RoundedTextFieldV2 addressField = new RoundedTextFieldV2(10, 1, COLOR_LINE_COLOR);
    protected static RoundedTextFieldV2 nidField = new RoundedTextFieldV2(10, 1, COLOR_LINE_COLOR);
    protected static RoundedTextFieldV2 salaryField = new RoundedTextFieldV2(10, 1, COLOR_LINE_COLOR);

    protected static DatePicker birthDatePicker = new DatePicker();
    protected JFormattedTextField birthdateEditor = new JFormattedTextField();
    protected static DatePicker hiredDatePicker = new DatePicker();
    protected JFormattedTextField hiredDateEditor = new JFormattedTextField();

    protected List<String> jobTitleOptions = new ArrayList<>(Arrays.asList("Manager", "Officer", "Waiter", "Chief", "Cashier", "General Worker", "Owner", "Sales", "Security", "Store Keeper"));
    protected List<String> genderOptions = new ArrayList<>(Arrays.asList("Male", "Female"));
    protected List<String> maritalStatusOptions = new ArrayList<>(Arrays.asList("Single", "Married", "Others"));

    protected static RoundedComboBox<String> jobTitleCombo = new RoundedComboBox<>(10, 1, COLOR_LINE_COLOR);
    protected static RoundedComboBox<String> genderCombo = new RoundedComboBox<>(10, 1, COLOR_LINE_COLOR);
    protected static RoundedComboBox<String> maritalCombo = new RoundedComboBox<>(10, 1, COLOR_LINE_COLOR);

    protected static EditableFoodImage addNewImageAvatar = new EditableFoodImage();
    protected IconTextButton createNewEmpButt = new IconTextButton("Create Employee", null, 10, COLOR_BUTTON_BLUE, 0);

    public EmpNewPage() {
    }

    @Override
    public RoundedPanel createEmpPage() {
        createEmpNewPage();
        return null;
    }

    public RoundedPanel createEmpNewPage() {

        empNewEntryPanel = new RoundedPanel(10);
        empNewEntryPanel.setLayout(new BorderLayout());
        empNewEntryPanel.setOpaque(false);
        empNewEntryPanel.add(getUpperPanel(), BorderLayout.NORTH);
        empNewEntryPanel.add(getBottomPanel(), BorderLayout.SOUTH);
        empNewEntryPanel.add(getUploadPanel(), BorderLayout.WEST);
        empNewEntryPanel.add(getTextFieldsHoldingPanel(), BorderLayout.CENTER);

        return empNewEntryPanel;
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

        JLabel pageLabel = new JLabel("New Employee");
        pageLabel.setFont(new Font(FlatRobotoFont.FAMILY, Font.BOLD, 16));
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

        bottom.add(EmpUIFactory.getCreateNewEmpButton(createNewEmpButt, COLOR_BUTTON_BLUE, getNewEmpInputData()));

        return bottom;
    }

    protected static JPanel upload;

    private JPanel getUploadPanel() {
        upload = new JPanel(new BorderLayout());
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

        center.add(EmpUIFactory.getFieldPanel("Name", nameField, "Enter Employee Name", false,false));
        center.add(EmpUIFactory.getPasswordField("Password", empTellerPasswordField, "Enter Password", false,true));
        center.add(EmpUIFactory.getFieldPanel("Father Name", fatherNameField, "Enter Employee's Father Name", false,false));
        center.add(EmpUIFactory.getFieldPanel("Mobile Number", mobileField, "Enter Mobile Number", false,false));
        center.add(EmpUIFactory.getFieldPanel("Address", addressField, "Enter Address", false,false));
        center.add(EmpUIFactory.getFieldPanel("National ID", nidField, "Enter NRC", false,false));
        center.add(EmpUIFactory.getFieldPanel("Salary", salaryField, "Enter Salary", true,false));

        center.add(EmpUIFactory.getDateFieldPanel("Date of Birth", birthDatePicker, birthdateEditor,false));
        center.add(EmpUIFactory.getDateFieldPanel("Hired Date", hiredDatePicker, hiredDateEditor,false));

        center.add(EmpUIFactory.getComboFieldPanel("Job Title", jobTitleCombo, jobTitleOptions,false));
        center.add(EmpUIFactory.getComboFieldPanel("Gender", genderCombo, genderOptions,false));
        center.add(EmpUIFactory.getComboFieldPanel("Marital Status", maritalCombo, maritalStatusOptions,false));

        return center;
    }

    public static void clearAllInputFields() {
        SwingUtilities.invokeLater(() -> {
            // Reset image avatar
            addNewImageAvatar.removeImage();
            addNewImageAvatar.setImage(EmpUIFactory.UPLOAD_BACKEND_IMG);

            // Clear text fields
            clearTextField(nameField);
            clearPasswordField(empTellerPasswordField);
            clearTextField(fatherNameField);
            clearTextField(mobileField);
            clearTextField(addressField);
            clearTextField(nidField);
            clearTextField(salaryField);

            // Clear date pickers
            clearDatePicker(birthDatePicker);
            clearDatePicker(hiredDatePicker);

            // Reset combo boxes
            resetComboBox(jobTitleCombo, "- Select -");
            resetComboBox(genderCombo, "- Select -");
            resetComboBox(maritalCombo, "- Select -");

            // Refresh the UI components
            refreshComponent(addNewImageAvatar, jobTitleCombo, genderCombo, maritalCombo);
        });
    }

    private static void clearTextField(RoundedTextFieldV2 textField) {
        if (textField != null) {
            textField.setText("");
        }
    }

    private static void clearPasswordField(RoundedPasswordField textField) {
        if (textField != null) {
            textField.setText("");
        }
    }

    private static void clearDatePicker(DatePicker datePicker) {
        if (datePicker != null) {
            datePicker.clearSelectedDate();
        }
    }

    private static void resetComboBox(RoundedComboBox<String> comboBox, String placeHolder) {
        if (comboBox != null) {
            // Set the selected value to null
            comboBox.setSelectedItem(placeHolder);
            comboBox.setPlaceholder(placeHolder);
            JTextField editorField = (JTextField) comboBox.getEditor().getEditorComponent();
            if (editorField != null) {
                editorField.setForeground(Color.GRAY); // Set initial placeholder color
            }
            // Optional: Ensure the UI reflects the change
            comboBox.repaint();
        }
    }


    private static void refreshComponent(JComponent... components) {
        for (JComponent component : components) {
            if (component != null) {
                component.revalidate();
                component.repaint();
            }
        }
    }

    private Runnable getNewEmpInputData() {
        return () -> {
            // Use SwingWorker to handle the background operation
            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() {
                    // Validate input fields
                    if (!areInputFieldsValid()) {
                        return null; // Skip processing if validation fails
                    }
                    try {
                        // Collect all input data and pass to the database method
                        dbBatchProcessNewEmp(
                                addNewImageAvatar.getImageBytes(),
                                nameField.getText(),
                                empTellerPasswordField.getPassword(),
                                fatherNameField.getText(),
                                mobileField.getText(),
                                addressField.getText(),
                                nidField.getText(),
                                salaryField.getText(),
                                birthDatePicker.getSelectedDate(),
                                hiredDatePicker.getSelectedDate(),
                                jobTitleCombo.getSelectedItem(),
                                genderCombo.getSelectedItem(),
                                maritalCombo.getSelectedItem()
                        );
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(EmpNewPage.empNewEntryPanel,
                                "Error while adding new employee. Please check the logs.",
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                    return null;
                }

                @Override
                protected void done() {
                    // Clear all input fields and show success message
                    EmpMainPage.refreshTableAfterEmpUpdater();

                }
            }.execute();
        };
    }

    private boolean areInputFieldsValid() {
        // Check if any of the required fields are empty or invalid
        if (addNewImageAvatar.getImageBytes() == null) {
            showError("Image must be provided.");
            return false;
        }
        if (nameField.getText().trim().isEmpty()) {
            showError("Name must be filled.");
            return false;
        }
        if (empTellerPasswordField.getPassword().length == 0) {
            showError("Password must be filled.");
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

    public void dbBatchProcessNewEmp(byte[] p_EmpImg,
                                     String p_EmpName,
                                     char[] rawPassword,
                                     String p_EmpFatherName,
                                     String p_MobileNo,
                                     String p_Address,
                                     String p_NRC,
                                     String p_Salary,
                                     LocalDate p_BirthDate,
                                     LocalDate p_HiredDate,
                                     Object p_Position,
                                     Object p_Gender,
                                     Object p_MaritalStatus) {
        String sql = "{CALL sp_CreateEmp(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
        String p_HashedPassword = PasswordUtils.hashPassword(new String(rawPassword));

        try (java.sql.Connection conn = DBConnection.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {


            stmt.setBytes(1, p_EmpImg);
            stmt.setString(2, p_EmpName);
            stmt.setString(3, p_HashedPassword);
            stmt.setString(4, p_EmpFatherName);
            stmt.setString(5, p_MobileNo);
            stmt.setString(6, p_Address);
            stmt.setString(7, p_NRC);
            stmt.setInt(8, trimAndParseNumber(p_Salary) != null ? trimAndParseNumber(p_Salary) : 0);
            stmt.setDate(9, p_BirthDate != null ? Date.valueOf(p_BirthDate) : null);
            stmt.setDate(10, p_HiredDate != null ? Date.valueOf(p_HiredDate) : null);
            stmt.setString(11, p_Position != null ? p_Position.toString() : "Unknown");
            stmt.setString(12, p_Gender != null ? p_Gender.toString() : "Unknown");
            stmt.setString(13, p_MaritalStatus != null ? p_MaritalStatus.toString() : "Unknown");
            stmt.setString(14, NetworkUtils.getLocalMachineName());
            stmt.setString(15, NetworkUtils.getCurrentIPAddress());

            // Execute the stored procedure
            stmt.execute();

            EmpNewPage.clearAllInputFields();
            JOptionPane.showMessageDialog(EmpNewPage.empNewEntryPanel,
                    "New Employee has been successfully added to the database.",
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
            throw new RuntimeException("Unexpected error occurred while processing new employee data.");
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



