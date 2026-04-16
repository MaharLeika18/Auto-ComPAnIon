package org.MiniDev.OOP;

public class CounterLists {
    private String counterName;
    private int counterID;
    private int counterLevel;
    private char activeYN;
    private String mainPrinterPortName;
    private String mainPrinterPortAddress;

    public CounterLists(String counterName, int counterID, int counterLevel, char activeYN, String mainPrinterPortName, String mainPrinterPortAddress) {
        this.counterName = counterName;
        this.counterID = counterID;
        this.counterLevel = counterLevel;
        this.activeYN = activeYN;
        this.mainPrinterPortName = mainPrinterPortName;
        this.mainPrinterPortAddress = mainPrinterPortAddress;
    }

    public String getCounterName() {
        return counterName;
    }

    public int getCounterID() {
        return counterID;
    }

    public int getCounterLevel() {
        return counterLevel;
    }

    public char getActiveYN() {
        return activeYN;
    }

    public String getMainPrinterPortName() {
        return mainPrinterPortName;
    }

    public String getMainPrinterPortAddress() {
        return mainPrinterPortAddress;
    }
}
