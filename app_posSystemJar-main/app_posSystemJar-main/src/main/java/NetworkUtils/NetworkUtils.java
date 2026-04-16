package NetworkUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class NetworkUtils {

    public static String getLocalMachineName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            System.err.println("Error retrieving machine name: " + e.getMessage());
            return "UnknownHost";
        }
    }

    public static String getCurrentIPAddress() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            System.err.println("Error retrieving IP address: " + e.getMessage());
            return "UnknownIP";
        }
    }

}
