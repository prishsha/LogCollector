import com.sun.net.httpserver.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;

// Serves the HTML dashboard on a given HTTP port
public class WebServer {

    private final int port;
    private final LogStore store;

    public WebServer(int port, LogStore store) {
        this.port  = port;
        this.store = store;
    }

    public void start() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", this::handleDashboard);
        server.setExecutor(null); // default executor
        server.start();
        System.out.println("[WebServer] Dashboard at http://localhost:" + port);
    }

    private void handleDashboard(HttpExchange ex) throws IOException {
        // Only serve GET /
        String method = ex.getRequestMethod();
        if (!method.equalsIgnoreCase("GET")) {
            ex.sendResponseHeaders(405, -1);
            return;
        }

        List<LogEvent> recent = store.getRecent(30);

        String html = HTMLBuilder.build(
            recent,
            store.getFailedServices(),
            store.getAllServices(),
            store.getTotalLogs(),
            store.getLogsPerSecond(),
            store.getUptimeSeconds(),
            store.getLogCounts()
        );

        byte[] bytes = html.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().set("Content-Type", "text/html; charset=utf-8");
        ex.sendResponseHeaders(200, bytes.length);
        try (OutputStream os = ex.getResponseBody()) {
            os.write(bytes);
        }
    }
}