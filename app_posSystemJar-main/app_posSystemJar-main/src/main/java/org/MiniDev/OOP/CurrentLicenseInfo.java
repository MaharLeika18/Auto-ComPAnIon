package org.MiniDev.OOP;

import java.sql.Timestamp;

public class CurrentLicenseInfo {
    private String keyCode;
    private String keyEditionName;
    private int keyDays;
    private Timestamp keyRegisterDate;
    private Timestamp keyExpiredDate;
    private String keyRegName;
    private String keyRegEmail;

    public CurrentLicenseInfo(String keyCode, String keyEditionName, int keyDays, Timestamp keyRegisterDate, Timestamp keyExpiredDate,
                              String keyRegName, String keyRegEmail) {
        this.keyCode = keyCode;
        this.keyEditionName = keyEditionName;
        this.keyDays = keyDays;
        this.keyRegisterDate = keyRegisterDate;
        this.keyExpiredDate = keyExpiredDate;
        this.keyRegName = keyRegName;
        this.keyRegEmail = keyRegEmail;
    }

    public void setKeyCode(String keyCode) {
        this.keyCode = keyCode;
    }

    public void setKeyEditionName(String keyEditionName) {
        this.keyEditionName = keyEditionName;
    }

    public void setKeyDays(int keyDays) {
        this.keyDays = keyDays;
    }

    public void setKeyRegisterDate(Timestamp keyRegisterDate) {
        this.keyRegisterDate = keyRegisterDate;
    }

    public void setKeyExpiredDate(Timestamp keyExpiredDate) {
        this.keyExpiredDate = keyExpiredDate;
    }

    public String getKeyRegName() {
        return keyRegName;
    }

    public void setKeyRegName(String keyRegName) {
        this.keyRegName = keyRegName;
    }

    public String getKeyRegEmail() {
        return keyRegEmail;
    }

    public void setKeyRegEmail(String keyRegEmail) {
        this.keyRegEmail = keyRegEmail;
    }

    public String getKeyCode() {
        return keyCode;
    }

    public String getKeyEditionName() {
        return keyEditionName;
    }

    public int getKeyDays() {
        return keyDays;
    }

    public Timestamp getKeyRegisterDate() {
        return keyRegisterDate;
    }

    public Timestamp getKeyExpiredDate() {
        return keyExpiredDate;
    }

    public String getKeyStatus() {
        Timestamp today = new Timestamp(System.currentTimeMillis());
        return keyExpiredDate.after(today) ? "Active" : "Inactive";
    }

    public String getDaysRemaining() {
        Timestamp today = new Timestamp(System.currentTimeMillis());
        long diffInMillis = keyExpiredDate.getTime() - today.getTime();

        // Convert milliseconds to days
        long diffInDays = diffInMillis / (1000 * 60 * 60 * 24);

        // Return the difference as an integer
        return diffInDays < 0 ? String.valueOf(0) : String.valueOf(diffInDays);
    }

    public boolean isActive() {
        Timestamp today = new Timestamp(System.currentTimeMillis());
        return keyExpiredDate.after(today); // Returns true if the key is expired
    }


}
