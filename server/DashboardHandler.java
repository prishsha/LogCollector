import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class DashboardHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        StringBuilder html = new StringBuilder();

        html.append("<html><head>");
        html.append("<meta http-equiv='refresh' content='2'>");
        html.append("</head><body>");

        html.append("<h1>Central Log Dashboard</h1>");

        // 🔹 Node status section
        html.append("<h2>Node Status</h2>");
        html.append("<table border='1'><tr><th>Service</th><th>Status</th></tr>");

        for (Map.Entry<String, String> entry : HeartbeatManager.status.entrySet()) {
            String color = entry.getValue().equals("UP") ? "green" : "red";
            html.append("<tr>")
                .append("<td>").append(entry.getKey()).append("</td>")
                .append("<td style='color:").append(color).append("'>")
                .append(entry.getValue())
                .append("</td>")
                .append("</tr>");
        }

        html.append("</table>");

        // 🔹 Logs section
        html.append("<h2>Logs</h2>");
        for (String log : LogHandler.logs) {
            html.append("<p>").append(log).append("</p>");
        }

        html.append("</body></html>");

        byte[] response = html.toString().getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(200, response.length);
        exchange.getResponseBody().write(response);
        exchange.getResponseBody().close();
    }
}
