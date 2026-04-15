//package Security;
//
//import java.util.Random;
//
//public class LicenseKeyGenerator {
//
//    private static final int PARTS = 4; // Number of groups
//    private static final int CHARACTERS_PER_PART = 4; // Characters per group
//    private static final String CHAR_POOL = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
//
//    public static void main(String[] args) {
//        for (int i = 0; i < 100; i++) {
//            String licenseKey = generateLicenseKey();
//            System.out.println(licenseKey);
//        }
//    }
//
//    public static String generateLicenseKey() {
//        StringBuilder licenseKey = new StringBuilder();
//        Random random = new Random();
//
//        for (int i = 0; i < PARTS; i++) {
//            for (int j = 0; j < CHARACTERS_PER_PART; j++) {
//                licenseKey.append(CHAR_POOL.charAt(random.nextInt(CHAR_POOL.length())));
//            }
//            if (i < PARTS - 1) {
//                licenseKey.append("-"); // Add hyphen after each part except the last
//            }
//        }
//        return licenseKey.toString();
//    }
//}
