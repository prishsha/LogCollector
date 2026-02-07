public class AlertManager {
    public static void check(String log) {
        if (log.contains("\"level\":\"ERROR\"")) {
            System.out.println("🚨 ALERT DETECTED: " + log);
        }
    }
}
