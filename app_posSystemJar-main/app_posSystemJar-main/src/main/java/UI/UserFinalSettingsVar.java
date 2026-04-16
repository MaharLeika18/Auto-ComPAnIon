package UI;


import java.awt.*;

public class UserFinalSettingsVar {
    public static final Color COLOR_GRAY = Color.decode("#f5f5f5");
    public static final Color COLOR_WHITE = Color.WHITE;
    public static final Color COLOR_ERROR_RED = Color.decode("#dc2626");
    public static final Color COLOR_ORANGE = Color.decode("#FC8019");
    public static final Color COLOR_GREEN = Color.decode("#09AA29");
    public static final Color COLOR_LINE_COLOR = Color.decode("#D1D1D1");
    public static final Color COLOR_FONT_GRAY = Color.decode("#9f9f9e");
    public static final Color COLOR_BUTTON_ORANGE = Color.decode("#fff2e8");
    public static final Color COLOR_BLUE = Color.decode("#4361ee");
    public static final Color COLOR_BLACK = Color.decode("#171717");
    public static final Color COLOR_PANEL_GRAY = Color.decode("#f6f6f6");
    public static final Color COLOR_PANEL_HOVER = Color.decode("#404040");
    public static double TAX_PERCENTAGE;
    public static String CURRENT_PROCESSING_ORDER_CODE = "";
    public static final Color COLOR_ENTRY_GRAY = Color.decode("#e5e5e5");
    public static final Color COLOR_SELECT_BLUE = Color.decode("#0284c7");
    public static final Color COLOR_BUTTON_BLUE = Color.decode("#4681f4");
    public static final Color COLOR_PANEL_BORDER_SILVER = Color.decode("#C0C0C0");

    public static void setTaxPercentage(double TAX_PERCENTAGE, boolean enableTaxCalculation) {
        if (enableTaxCalculation) {
            UserFinalSettingsVar.TAX_PERCENTAGE = TAX_PERCENTAGE;
        } else {
            UserFinalSettingsVar.TAX_PERCENTAGE = 0.0;
        }
    }
}
