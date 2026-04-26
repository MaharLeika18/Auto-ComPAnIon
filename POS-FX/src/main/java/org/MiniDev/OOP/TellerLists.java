package org.MiniDev.OOP;

public class TellerLists {
    private int tellerID;
    private String tellerName;

    public TellerLists(int tellerID, String tellerName) {
        this.tellerID = tellerID;
        this.tellerName = tellerName;
    }

    public int getTellerID() {
        return tellerID;
    }

    public void setTellerID(int tellerID) {
        this.tellerID = tellerID;
    }

    public String getTellerName() {
        return tellerName;
    }

    public void setTellerName(String tellerName) {
        this.tellerName = tellerName;
    }
}
