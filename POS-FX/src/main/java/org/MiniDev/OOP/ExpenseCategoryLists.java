package org.MiniDev.OOP;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.Date;
import java.text.DecimalFormat;

/**
 * Represents an expense category with associated data, including an icon,
 * code, name, last updated date, and active status.
 */
public class ExpenseCategoryLists {

    protected byte[] iconExpense;
    protected String expenseCode;
    protected String expenseCodeName;
    protected Date lastUpdatedDate;
    protected char currentUseYN;
    protected int currentYearBudgetAmount;

    /**
     * Constructs an ExpenseCategoryLists object with the specified parameters.
     *
     * @param iconExpense             Byte array representing the expense category icon.
     * @param expenseCode             Unique code of the expense category.
     * @param expenseCodeName         Name associated with the expense category.
     * @param lastUpdatedDate         Date the category was last updated.
     * @param currentUseYN            Indicates if the category is currently in use ('Y' or 'N').
     * @param currentYearBudgetAmount Indicates this is only current year only allocation budget amount.
     */
    public ExpenseCategoryLists(byte[] iconExpense, String expenseCode, String expenseCodeName,
                                Date lastUpdatedDate, char currentUseYN, int currentYearBudgetAmount) {
        this.iconExpense = iconExpense;
        this.expenseCode = expenseCode;
        this.expenseCodeName = expenseCodeName;
        this.lastUpdatedDate = lastUpdatedDate;
        this.currentUseYN = currentUseYN;
        this.currentYearBudgetAmount = currentYearBudgetAmount;
    }

    DecimalFormat decimalFormat = new DecimalFormat("#,###");

    public int getCurrentYearBudgetAmount() {
        return currentYearBudgetAmount;
    }

    public String getCurrentYearBudgetAmountString() {
        return decimalFormat.format(currentYearBudgetAmount);
    }

    public String getExpUsedActiveInactive() {
        if (currentUseYN == 'Y') {
            return "Active";
        } else {
            return "Inactive";
        }
    }

    /**
     * @return Byte array representing the icon for this expense category.
     */
    public byte[] getIconExpense() {
        return iconExpense;
    }

    /**
     * @return Unique code of the expense category.
     */
    public String getExpenseCode() {
        return expenseCode;
    }

    /**
     * @return Name of the expense category.
     */
    public String getExpenseCodeName() {
        return expenseCodeName;
    }

    /**
     * @return Date when the expense category was last updated.
     */
    public Date getLastUpdatedDate() {
        return lastUpdatedDate;
    }

    /**
     * @return 'Y' if the category is currently in use, 'N' otherwise.
     */
    public char getCurrentUseYN() {
        return currentUseYN;
    }

    /**
     * Converts the iconExpense byte array into an ImageIcon for use in the UI.
     *
     * @return Icon created from the byte array, or null if conversion fails.
     */
    public Icon getIconExpenseAsIcon() {
        if (iconExpense == null || iconExpense.length == 0) {
            return null; // Handle missing or empty icon data
        }
        try (ByteArrayInputStream bais = new ByteArrayInputStream(iconExpense)) {
            BufferedImage bufferedImage = ImageIO.read(bais);

            if (bufferedImage != null) {
                // Optional: Adjust image size as needed
                Image scaledImage = bufferedImage.getScaledInstance(40, 40, Image.SCALE_SMOOTH);
                return new ImageIcon(scaledImage); // Convert to ImageIcon
            }
        } catch (IOException e) {
            e.printStackTrace(); // Log the error if image conversion fails
        }
        return null; // Return null if conversion fails
    }
}
