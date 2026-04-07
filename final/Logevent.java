import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

// Simple data class representing a single log entry from a service
public class LogEvent {
    public final String service;
    public final String level;   // INFO or ERROR
    public final String message;
    public final long timestamp;

    private static final DateTimeFormatter FMT =
        DateTimeFormatter.ofPattern("HH:mm:ss").withZone(ZoneId.systemDefault());

    public LogEvent(String service, String level, String message, long timestamp) {
        this.service   = service;
        this.level     = level;
        this.message   = message;
        this.timestamp = timestamp;
    }

    // Parse a pipe-delimited line sent over TCP
    public static LogEvent parse(String raw) {
        String[] parts = raw.split("\\|", 4);
        if (parts.length < 4) return null;
        try {
            return new LogEvent(parts[0].trim(), parts[1].trim(),
                                parts[2].trim(), Long.parseLong(parts[3].trim()));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public String formattedTime() {
        return FMT.format(Instant.ofEpochMilli(timestamp));
    }

    @Override
    public String toString() {
        return "[" + formattedTime() + "] " + service + " " + level + " - " + message;
    }
}