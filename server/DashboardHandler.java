import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;

public class DashboardHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        StringBuilder html = new StringBuilder();

        html.append("<html><head>");
        html.append("<meta http-equiv='refresh' content='2'>");
        html.append("</head><body>");

        html.append("<h1>Central Log Dashboard</h1>");

        for (String log : LogHandler.logs) {
            html.append("<p>").append(log).append("</p>");
        }

        html.append("</body></html>");

        byte[] response = html.toString().getBytes();
        exchange.sendResponseHeaders(200, response.length);
        exchange.getResponseBody().write(response);
        exchange.close();
    }
}
