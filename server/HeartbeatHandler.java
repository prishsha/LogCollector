import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;

public class HeartbeatHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        if ("POST".equals(exchange.getRequestMethod())) {

            String body = new String(exchange.getRequestBody().readAllBytes());

            // Extract service name (simple parsing)
            String service = body.replace("\"", "").split(":")[1];

            HeartbeatManager.updateHeartbeat(service);

            String response = "Heartbeat received";
            exchange.sendResponseHeaders(200, response.length());
            exchange.getResponseBody().write(response.getBytes());
            exchange.close();
        }
    }
}
