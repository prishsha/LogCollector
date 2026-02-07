package worker;
import java.util.Random;

public class LogGenerator {
    static String[] levels = {"INFO", "WARN", "ERROR"};

    public static String generate() {
        return "{"
                + "\"service\":\"order-service\","
                + "\"level\":\"" + levels[new Random().nextInt(3)] + "\","
                + "\"message\":\"Order processed\","
                + "\"timestamp\":" + System.currentTimeMillis()
                + "}";
    }
}
