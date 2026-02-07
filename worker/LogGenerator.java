import java.util.Random;

public class LogGenerator {

    static String[] levels = {"INFO", "WARN", "ERROR"};

    public static String generate(String serviceName) {

        return "{"
                + "\"service\":\"" + serviceName + "\","
                + "\"level\":\"" + levels[new Random().nextInt(3)] + "\","
                + "\"message\":\"Operation executed\","
                + "\"timestamp\":" + System.currentTimeMillis()
                + "}";
    }
}
