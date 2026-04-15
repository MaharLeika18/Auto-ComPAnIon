package Utils;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.bluetooth.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PrinterDiscovery {

    public static List<String> getLocalNetworkPrinters() {
        List<String> printerInfoList = new ArrayList<>();

        // Get all available print services
        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
        for (PrintService printService : printServices) {
            String printerName = printService.getName();
            // Optionally get the address if you have a way to do so
            // In most cases, local printers may not expose an address in this way
            printerInfoList.add(printerName);
        }

        return printerInfoList;
    }

    public static List<String> discoverBluetoothPrinters() {
        List<String> bluetoothDevices = new ArrayList<>();

        try {
            final Object inquiryCompletedEvent = new Object();
            DiscoveryListener listener = new DiscoveryListener() {
                public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
                    try {
                        String friendlyName = btDevice.getFriendlyName(false);
                        bluetoothDevices.add(friendlyName);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                public void inquiryCompleted(int discType) {
                    synchronized (inquiryCompletedEvent) {
                        inquiryCompletedEvent.notify();
                    }
                }

                public void serviceSearchCompleted(int transID, int respCode) {}
                public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {}
            };

            DiscoveryAgent agent = LocalDevice.getLocalDevice().getDiscoveryAgent();
            agent.startInquiry(DiscoveryAgent.PREKNOWN, listener);

            synchronized (inquiryCompletedEvent) {
                inquiryCompletedEvent.wait();
            }

        } catch (BluetoothStateException e) {

        } catch (Exception e) {
            e.printStackTrace();
        }

        return bluetoothDevices;
    }

    public static List<String> getAllPrinters() {
        Set<String> allPrinters = new HashSet<>();
        allPrinters.addAll(getLocalNetworkPrinters());
        allPrinters.addAll(discoverBluetoothPrinters());

        return new ArrayList<>(allPrinters); // Convert to List to return
    }
}
