import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;

public class LogServer {

    public static void main(String[] args) throws Exception {

        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        server.createContext("/log", new LogHandler());
        server.createContext("/heartbeat", new HeartbeatHandler());
        server.createContext("/", new DashboardHandler());

        server.setExecutor(null);

        // Failure detection thread
        new Thread(() -> {
            while (true) {
                HeartbeatManager.checkFailures();
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        server.start();

        System.out.println("Name: Prisha Vadhavkar");
        System.out.println("Registration Number: 23BIT0010");
        System.out.println("Central Log Server running on port 8080");

    }
}
