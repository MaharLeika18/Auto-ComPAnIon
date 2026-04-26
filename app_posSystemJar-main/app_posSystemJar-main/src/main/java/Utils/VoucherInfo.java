package Utils;

public class VoucherInfo {

    public static String companyName;
    public static String addressLine1;
    public static String addressLine2;
    public static String phoneNumber;
    public static String emailAddress;
    public static String afterSaleLabelForVoucher;
    public static String developmentCompanyNameForVoucher = "Powered by MiniDev POS";

    public VoucherInfo(String companyName, String addressLine1, String addressLine2, String phoneNumber, String emailAddress, String afterSaleLabelForVoucher, String developmentCompanyNameForVoucher) {
        this.companyName = companyName;
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.phoneNumber = phoneNumber;
        this.emailAddress = emailAddress;
        this.afterSaleLabelForVoucher = afterSaleLabelForVoucher;
        this.developmentCompanyNameForVoucher = developmentCompanyNameForVoucher;
    }

    public VoucherInfo() {};

    public String getCompanyName() {
        return companyName;
    }

    public  void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public  void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public  void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public  void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getAfterSaleLabelForVoucher() {
        return afterSaleLabelForVoucher;
    }

    public void setAfterSaleLabelForVoucher(String afterSaleLabelForVoucher) {
        this.afterSaleLabelForVoucher = afterSaleLabelForVoucher;
    }

    public String getDevelopmentCompanyNameForVoucher() {
        return developmentCompanyNameForVoucher;
    }
}
