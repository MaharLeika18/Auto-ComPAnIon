package org.MiniDev.Backoffice.GeneralSettings.SettingsModel;

public class GeneralSettingsModel {
    private String biz_name;
    private String biz_address;
    private String biz_townCity;
    private String biz_phNo;
    private String biz_mail;
    private char tax_cal_YN;
    private double tax_sales_rate;
    private char cash_YN;
    private char bank_YN;
    private char mobilePay_YN;
    private char giftCard_YN;
    private char admin_access_YN;
    private char manager_access_YN;
    private char cashier_access_YN;
    private char stock_tracking_YN;
    private int lowStock_num_alert;
    private char auto_reorder_YN;
    private String receipt_header;
    private String receipt_footer;
    private char itemized_receipt_YN;
    private char print_receipt_auto_YN;
    private char noti_email_YN;
    private char noti_sms_YN;
    private char table_need_YN;

    public GeneralSettingsModel(String biz_name, String biz_address,String biz_townCity, String biz_phNo, String biz_mail, char tax_cal_YN, double tax_sales_rate, char cash_YN, char bank_YN, char mobilepay_YN, char giftcard_YN, char admin_access_YN, char manager_access_YN, char cashier_access_YN, char stock_tracking_YN, int lowstock_num_alert, char auto_reorder_YN, String receipt_header, String receipt_footer, char itemized_receipt_YN, char print_receipt_auto_YN, char noti_email_YN, char noti_sms_YN, char table_need_YN) {
        this.biz_name = biz_name;
        this.biz_address = biz_address;
        this.biz_townCity = biz_townCity;
        this.biz_phNo = biz_phNo;
        this.biz_mail = biz_mail;
        this.tax_cal_YN = tax_cal_YN;
        this.tax_sales_rate = tax_sales_rate;
        this.cash_YN = cash_YN;
        this.bank_YN = bank_YN;
        this.mobilePay_YN = mobilepay_YN;
        this.giftCard_YN = giftcard_YN;
        this.admin_access_YN = admin_access_YN;
        this.manager_access_YN = manager_access_YN;
        this.cashier_access_YN = cashier_access_YN;
        this.stock_tracking_YN = stock_tracking_YN;
        this.lowStock_num_alert = lowstock_num_alert;
        this.auto_reorder_YN = auto_reorder_YN;
        this.receipt_header = receipt_header;
        this.receipt_footer = receipt_footer;
        this.itemized_receipt_YN = itemized_receipt_YN;
        this.print_receipt_auto_YN = print_receipt_auto_YN;
        this.noti_email_YN = noti_email_YN;
        this.noti_sms_YN = noti_sms_YN;
        this.table_need_YN = table_need_YN;
    }

    public String getBiz_townCity() {
        return biz_townCity;
    }

    public void setBiz_townCity(String biz_townCity) {
        this.biz_townCity = biz_townCity;
    }

    public String getBiz_name() {
        return biz_name;
    }

    public void setBiz_name(String biz_name) {
        this.biz_name = biz_name;
    }

    public String getBiz_address() {
        return biz_address;
    }

    public void setBiz_address(String biz_address) {
        this.biz_address = biz_address;
    }

    public String getBiz_phNo() {
        return biz_phNo;
    }

    public void setBiz_phNo(String biz_phNo) {
        this.biz_phNo = biz_phNo;
    }

    public String getBiz_mail() {
        return biz_mail;
    }

    public void setBiz_mail(String biz_mail) {
        this.biz_mail = biz_mail;
    }

    public char getTax_cal_YN() {
        return tax_cal_YN;
    }

    public void setTax_cal_YN(char tax_cal_YN) {
        this.tax_cal_YN = tax_cal_YN;
    }

    public double getTax_sales_rate() {
        return tax_sales_rate;
    }

    public void setTax_sales_rate(double tax_sales_rate) {
        this.tax_sales_rate = tax_sales_rate;
    }

    public char getCash_YN() {
        return cash_YN;
    }

    public void setCash_YN(char cash_YN) {
        this.cash_YN = cash_YN;
    }

    public char getBank_YN() {
        return bank_YN;
    }

    public void setBank_YN(char bank_YN) {
        this.bank_YN = bank_YN;
    }

    public char getMobilePay_YN() {
        return mobilePay_YN;
    }

    public void setMobilePay_YN(char mobilePay_YN) {
        this.mobilePay_YN = mobilePay_YN;
    }

    public char getGiftCard_YN() {
        return giftCard_YN;
    }

    public void setGiftCard_YN(char giftCard_YN) {
        this.giftCard_YN = giftCard_YN;
    }

    public char getAdmin_access_YN() {
        return admin_access_YN;
    }

    public void setAdmin_access_YN(char admin_access_YN) {
        this.admin_access_YN = admin_access_YN;
    }

    public char getManager_access_YN() {
        return manager_access_YN;
    }

    public void setManager_access_YN(char manager_access_YN) {
        this.manager_access_YN = manager_access_YN;
    }

    public char getCashier_access_YN() {
        return cashier_access_YN;
    }

    public void setCashier_access_YN(char cashier_access_YN) {
        this.cashier_access_YN = cashier_access_YN;
    }

    public char getStock_tracking_YN() {
        return stock_tracking_YN;
    }

    public void setStock_tracking_YN(char stock_tracking_YN) {
        this.stock_tracking_YN = stock_tracking_YN;
    }

    public int getLowStock_num_alert() {
        return lowStock_num_alert;
    }

    public void setLowStock_num_alert(int lowStock_num_alert) {
        this.lowStock_num_alert = lowStock_num_alert;
    }

    public char getAuto_reorder_YN() {
        return auto_reorder_YN;
    }

    public void setAuto_reorder_YN(char auto_reorder_YN) {
        this.auto_reorder_YN = auto_reorder_YN;
    }

    public String getReceipt_header() {
        return receipt_header;
    }

    public void setReceipt_header(String receipt_header) {
        this.receipt_header = receipt_header;
    }

    public String getReceipt_footer() {
        return receipt_footer;
    }

    public void setReceipt_footer(String receipt_footer) {
        this.receipt_footer = receipt_footer;
    }

    public char getItemized_receipt_YN() {
        return itemized_receipt_YN;
    }

    public void setItemized_receipt_YN(char itemized_receipt_YN) {
        this.itemized_receipt_YN = itemized_receipt_YN;
    }

    public char getPrint_receipt_auto_YN() {
        return print_receipt_auto_YN;
    }

    public void setPrint_receipt_auto_YN(char print_receipt_auto_YN) {
        this.print_receipt_auto_YN = print_receipt_auto_YN;
    }

    public char getNoti_email_YN() {
        return noti_email_YN;
    }

    public void setNoti_email_YN(char noti_email_YN) {
        this.noti_email_YN = noti_email_YN;
    }

    public char getNoti_sms_YN() {
        return noti_sms_YN;
    }

    public void setNoti_sms_YN(char noti_sms_YN) {
        this.noti_sms_YN = noti_sms_YN;
    }

    public char getTable_need_YN() {
        return table_need_YN;
    }

    public void setTable_need_YN(char table_need_YN) {
        this.table_need_YN = table_need_YN;
    }
}
