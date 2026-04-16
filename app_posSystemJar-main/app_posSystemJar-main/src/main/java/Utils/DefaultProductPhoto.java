package Utils;

import org.MiniDev.Home.CreateHomePanel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class DefaultProductPhoto {
    public static byte[] getDefaultPhotoBytes() {
        try (InputStream is = CreateHomePanel.class.getResourceAsStream("/DefaultFoodIcon.png")) {
            if (is == null) {
                System.err.println("Default icon not found.");
                return null; // or handle the error gracefully
            }
            // Read the image content into a byte array
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int nRead;
            byte[] data = new byte[1024];
            while ((nRead = is.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            buffer.flush();
            return buffer.toByteArray();

        } catch (IOException e) {
            System.err.println("Failed to read default icon file: " + e.getMessage());
            return null; // or handle the error gracefully
        }
    }
}
