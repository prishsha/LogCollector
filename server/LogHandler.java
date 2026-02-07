import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.*;
import java.util.*;

public class LogHandler implements HttpHandler {

    public static List<String> logs = new ArrayList<>();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("POST".equals(exchange.getRequestMethod())) {

            String body = new String(exchange.getRequestBody().readAllBytes());
            logs.add(body);

            AlertManager.check(body);

            String response = "Log received";
            exchange.sendResponseHeaders(200, response.length());
            exchange.getResponseBody().write(response.getBytes());
            exchange.close();
        }
    }
}
