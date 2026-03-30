import java.io.*;
import java.net.*;
import java.util.concurrent.*;

// Listens for incoming TCP connections from worker nodes
// Each connection runs in its own thread
public class TCPLogServer {

    private final int port;
    private final LogStore store;
    private final ExecutorService pool = Executors.newCachedThreadPool();

    public TCPLogServer(int port, LogStore store) {
        this.port  = port;
        this.store = store;
    }

    public void start() throws IOException {
        ServerSocket server = new ServerSocket(port);
        System.out.println("[TCPLogServer] Listening on port " + port);

        // accept loop runs in its own thread so it doesn't block main
        new Thread(() -> {
            while (true) {
                try {
                    Socket client = server.accept();
                    pool.submit(() -> handleClient(client));
                } catch (IOException e) {
                    System.err.println("[TCPLogServer] Accept error: " + e.getMessage());
                }
            }
        }, "tcp-accept").start();
    }

    private void handleClient(Socket client) {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(client.getInputStream()))) {

            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                LogEvent event = LogEvent.parse(line);
                if (event != null) {
                    store.add(event);
                } else {
                    System.err.println("[TCPLogServer] Bad log line: " + line);
                }
            }
        } catch (IOException e) {
            // client disconnected — normal
        }
    }
}