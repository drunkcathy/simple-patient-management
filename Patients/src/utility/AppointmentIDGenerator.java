package utility;

import java.util.UUID;

public class AppointmentIDGenerator {

    public static String generateID() {
        String randomPart = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        return "A-P" + randomPart;
    }
}