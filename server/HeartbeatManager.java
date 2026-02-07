
import java.util.HashMap;
import java.util.Map;

public class HeartbeatManager {

    // serviceName -> last heartbeat time
    public static Map<String, Long> heartbeats = new HashMap<>();

    public static void updateHeartbeat(String serviceName) {
        heartbeats.put(serviceName, System.currentTimeMillis());
    }

    public static void checkFailures() {
        long now = System.currentTimeMillis();

        for (String service : heartbeats.keySet()) {
            long lastSeen = heartbeats.get(service);

            if (now - lastSeen > 5000) { // 5 seconds timeout
                System.out.println("NODE FAILURE DETECTED: " + service);
            }
        }
    }
}
