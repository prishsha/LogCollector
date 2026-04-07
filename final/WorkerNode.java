import java.io.*;
import java.net.*;
import java.util.Random;

// Simulates a microservice that generates log events and sends them via TCP
public class WorkerNode implements Runnable {

    private final String serviceName;
    private final int tcpPort;
    private final String[] infoMessages;
    private final String[] errorMessages;
    private final Random rand = new Random();

    // interval between log sends (ms)
    private final int minDelay;
    private final int maxDelay;

    public WorkerNode(String serviceName, int tcpPort,
                      String[] infoMessages, String[] errorMessages,
                      int minDelay, int maxDelay) {
        this.serviceName   = serviceName;
        this.tcpPort       = tcpPort;
        this.infoMessages  = infoMessages;
        this.errorMessages = errorMessages;
        this.minDelay      = minDelay;
        this.maxDelay      = maxDelay;
    }

    @Override
    public void run() {
        // retry loop — keeps trying to connect if server isn't ready
        while (!Thread.currentThread().isInterrupted()) {
            String host = System.getenv().getOrDefault("SERVER_HOST", "localhost");
            try (
                Socket socket = new Socket(host, tcpPort);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

                System.out.println("[WorkerNode] " + serviceName + " connected.");

                while (!Thread.currentThread().isInterrupted()) {
                    String level;
                    String msg;

                    // ~15% chance of an error log
                    if (rand.nextInt(100) < 15) {
                        level = "ERROR";
                        msg = errorMessages[rand.nextInt(errorMessages.length)];
                    } else {
                        level = "INFO";
                        msg = infoMessages[rand.nextInt(infoMessages.length)];
                    }

                    String line = serviceName + "|" + level + "|" + msg + "|" + System.currentTimeMillis();
                    out.println(line);

                    int delay = minDelay + rand.nextInt(maxDelay - minDelay + 1);
                    Thread.sleep(delay);
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (IOException e) {
                // server not ready yet, wait and retry
                try { Thread.sleep(2000); } catch (InterruptedException ie) { break; }
            }
        }
    }
}