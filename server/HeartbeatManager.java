import java.util.HashMap;
import java.util.Map;

public class HeartbeatManager {

    // service -> last heartbeat time
    public static Map<String, Long> heartbeats = new HashMap<>();

    // service -> status (UP / DOWN)
    public static Map<String, String> status = new HashMap<>();

    public static void updateHeartbeat(String service) {
        heartbeats.put(service, System.currentTimeMillis());
        status.put(service, "UP");
    }

    public static void checkFailures() {
        long now = System.currentTimeMillis();

        for (String service : heartbeats.keySet()) {
            long lastSeen = heartbeats.get(service);

            if (now - lastSeen > 5000) {
                if (!"DOWN".equals(status.get(service))) {
                    status.put(service, "DOWN");
                    System.out.println("NODE FAILURE DETECTED: " + service);
                }
            }
        }
    }
}
