package org.MiniDev.OOP;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.time.LocalDate;
import java.time.Period;
import java.util.HashMap;
import java.util.Map;

public class EmployeeHistoryLists {
    private byte[] employeePhoto;
    private int employeeId;
    private String employeeName;
    private String hashedPassword;
    private String departmentName;
    private String telNo;
    private Date hireDate;
    private Date retiredDate;
    private String nrc;
    private Date birthDate;
    private String homeAddress;
    private String fatherName;
    private int currentFixedSalaryAmount;
    private char activeYN;
    private char genderMW;
    private String maritalStatus;
    private static byte[] defaultPhoto;

    static {
        try (InputStream inputStream = EmployeeHistoryLists.class.getResourceAsStream("/EmployeeDefault.jpg")) {
            if (inputStream != null) {
                defaultPhoto = new byte[inputStream.available()];
                inputStream.read(defaultPhoto);
            } else {
                // Handle missing resource case
                System.err.println("Default photo not found.");
                defaultPhoto = new byte[0]; // or set a placeholder image byte array
            }
        } catch (IOException e) {
            e.printStackTrace();
            defaultPhoto = new byte[0]; // or handle the error as needed
        }
    }

    public String getTelNo() {
        return telNo;
    }

    public Date getHireDate() {
        return hireDate;
    }

    public Date getRetiredDate() {
        return retiredDate;
    }

    public static byte[] getDefaultPhoto() {
        return defaultPhoto;
    }

    public EmployeeHistoryLists(byte[] employeePhoto, int employeeId, String employeeName, String hashedPassword, String departmentName,
                                String telNo, Date hireDate, Date retiredDate, String nrc, Date birthDate,
                                String homeAddress, String fatherName, int currentFixedSalaryAmount,
                                char activeYN, char genderMW, String maritalStatus) {
        this.employeePhoto = (employeePhoto != null) ? employeePhoto : defaultPhoto; // Use default if null
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.hashedPassword = hashedPassword;
        this.departmentName = departmentName;
        this.telNo = telNo;
        this.hireDate = hireDate;
        this.retiredDate = retiredDate;
        this.nrc = nrc;
        this.birthDate = birthDate;
        this.homeAddress = homeAddress;
        this.fatherName = fatherName;
        this.currentFixedSalaryAmount = currentFixedSalaryAmount;
        this.activeYN = activeYN;
        this.genderMW = genderMW;
        this.maritalStatus = maritalStatus;
    }

    public String getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public void setEmployeePhoto(byte[] employeePhoto) {
        this.employeePhoto = employeePhoto;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getRawPassword() {
        return hashedPassword;
    }

    public void setRawPassword(String rawPassword) {
        this.hashedPassword = rawPassword;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public void setTelNo(String telNo) {
        this.telNo = telNo;
    }

    public void setHireDate(Date hireDate) {
        this.hireDate = hireDate;
    }

    public void setRetiredDate(Date retiredDate) {
        this.retiredDate = retiredDate;
    }

    public void setNrc(String nrc) {
        this.nrc = nrc;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public void setHomeAddress(String homeAddress) {
        this.homeAddress = homeAddress;
    }

    public void setFatherName(String fatherName) {
        this.fatherName = fatherName;
    }

    public void setCurrentFixedSalaryAmount(int currentFixedSalaryAmount) {
        this.currentFixedSalaryAmount = currentFixedSalaryAmount;
    }

    public void setActiveYN(char activeYN) {
        this.activeYN = activeYN;
    }

    public void setGenderMW(char genderMW) {
        this.genderMW = genderMW;
    }

    public static void setDefaultPhoto(byte[] defaultPhoto) {
        EmployeeHistoryLists.defaultPhoto = defaultPhoto;
    }

    public Map<Integer, Icon> getEmpIconCache() {
        return empIconCache;
    }

    public void setEmpIconCache(Map<Integer, Icon> empIconCache) {
        this.empIconCache = empIconCache;
    }

    public byte[] getEmployeePhoto() {
        return employeePhoto; // No need for null check here; it's already handled in the constructor
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public String getEmployeeName() {
        return (genderMW == 'M') ? "Mg " + employeeName : "Ma " + employeeName;
    }

    public String getEmployeeNameID() {
        return employeeName;
    }

    public int getAge() {
        if (birthDate == null) {
            return -1; // or throw an exception if preferred
        }
        LocalDate birthDateLocal = birthDate.toLocalDate();
        LocalDate today = LocalDate.now();
        return Period.between(birthDateLocal, today).getYears();
    }

    public String getEmployeeTelNo() {
        return telNo;
    }

    public Date getHiredDate() {
        return hireDate;
    }

    public String getNrc() {
        return nrc;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public String getHomeAddress() {
        return homeAddress;
    }

    public String getFatherName() {
        return fatherName;
    }

    public int getCurrentFixedSalaryAmount() {
        return currentFixedSalaryAmount;
    }

    public char getActiveYN() {
        return activeYN;
    }

    public char getGenderMW() {
        return genderMW;
    }

    private Map<Integer, Icon> empIconCache = new HashMap<>(); // Cache for icons

    public Icon getEmpProfileAsIcon() {
        if (employeePhoto == null || employeePhoto.length == 0) {
            return null; // Handle missing or empty icon data
        }

        // Check the cache first
        if (empIconCache.containsKey(employeeId)) {
            return empIconCache.get(employeeId);
        }

        try (ByteArrayInputStream bais = new ByteArrayInputStream(employeePhoto)) {
            BufferedImage bufferedImage = ImageIO.read(bais);

            if (bufferedImage != null) {
                Image scaledImage = bufferedImage.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                Icon icon = new ImageIcon(scaledImage);
                empIconCache.put(employeeId, icon); // Cache the generated icon
                return icon;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


}
