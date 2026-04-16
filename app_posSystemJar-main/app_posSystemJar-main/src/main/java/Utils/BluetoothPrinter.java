package Utils;

public class BluetoothPrinter {

    private String address;
    private String printerName;
    private String printerModel;

    public BluetoothPrinter(String printerName,String printerModel,String address) {
        this.printerName = printerName;
        this.printerModel = printerModel;
        this.address = address;
    }

    public BluetoothPrinter(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPrinterName() {
        return printerName;
    }

    public void setPrinterName(String printerName) {
        this.printerName = printerName;
    }

    public String getPrinterModel() {
        return printerModel;
    }

    public void setPrinterModel(String printerModel) {
        this.printerModel = printerModel;
    }

    public void sendData(byte[] data) {
        // Implement Bluetooth communication logic to send data to the printer
    }
}
