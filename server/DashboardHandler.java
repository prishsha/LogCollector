import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;

public class DashboardHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        StringBuilder html = new StringBuilder();
        html.append("<html><body><h1>Central Log Dashboard</h1>");

        for (String log : LogHandler.logs) {
            html.append("<p>").append(log).append("</p>");
        }

        html.append("</body></html>");

        exchange.sendResponseHeaders(200, html.length());
        exchange.getResponseBody().write(html.toString().getBytes());
        exchange.close();
    }
}
