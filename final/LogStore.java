import java.util.*;
import java.util.concurrent.*;

// Stores logs and tracks service health
// Thread-safe via ConcurrentLinkedDeque + ConcurrentHashMap
public class LogStore {

    private static final int MAX_LOGS = 200;
    private static final long FAILURE_TIMEOUT_MS = 6000;

    private final Deque<LogEvent> logs = new ConcurrentLinkedDeque<>();
    // last heartbeat time per service
    private final ConcurrentHashMap<String, Long> lastSeen = new ConcurrentHashMap<>();
    // total counts
    private final ConcurrentHashMap<String, Integer> logCounts = new ConcurrentHashMap<>();
    private long totalLogs = 0;
    private long startTime = System.currentTimeMillis();

    public void add(LogEvent event) {
        logs.addFirst(event);
        while (logs.size() > MAX_LOGS) logs.removeLast();

        lastSeen.put(event.service, event.timestamp);
        logCounts.merge(event.service, 1, Integer::sum);
        totalLogs++;
    }

    // Returns a snapshot of the latest N logs (newest first)
    public List<LogEvent> getRecent(int n) {
        List<LogEvent> result = new ArrayList<>();
        int count = 0;
        for (LogEvent e : logs) {
            if (count++ >= n) break;
            result.add(e);
        }
        return result;
    }

    // Returns set of service names that haven't sent a log recently
    public Set<String> getFailedServices() {
        Set<String> failed = new LinkedHashSet<>();
        long now = System.currentTimeMillis();
        for (Map.Entry<String, Long> entry : lastSeen.entrySet()) {
            if (now - entry.getValue() > FAILURE_TIMEOUT_MS) {
                failed.add(entry.getKey());
            }
        }
        return failed;
    }

    // All known services
    public Set<String> getAllServices() {
        return lastSeen.keySet();
    }

    public long getTotalLogs() { return totalLogs; }

    public long getUptimeSeconds() {
        return (System.currentTimeMillis() - startTime) / 1000;
    }

    // Rough logs-per-second since startup
    public double getLogsPerSecond() {
        long uptime = getUptimeSeconds();
        return uptime == 0 ? 0 : (double) totalLogs / uptime;
    }

    public Map<String, Integer> getLogCounts() {
        return Collections.unmodifiableMap(logCounts);
    }

    public List<LogEvent> getAllLogs() {
    return new ArrayList<>(logs);
}
}