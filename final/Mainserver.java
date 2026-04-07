// Entry point for the E-Commerce Monitoring System
// Starts the TCP log server, HTTP dashboard, and all worker node simulators
public class MainServer {

    static final int TCP_PORT  = 9000;
    static final int HTTP_PORT = 8080;

    public static void main(String[] args) throws Exception {

        LogStore   store    = new LogStore();
        TCPLogServer tcp    = new TCPLogServer(TCP_PORT, store);
        WebServer  web      = new WebServer(HTTP_PORT, store);

        tcp.start();
        web.start();

        // Give the TCP server a moment to bind before workers connect
        Thread.sleep(500);

        startWorker("AuthService",
            new String[]{
                "User login successful",
                "Session token issued",
                "Password reset email sent",
                "OAuth2 token refreshed",
                "New user registered"
            },
            new String[]{
                "Login failed: invalid credentials",
                "Session expired, forcing re-auth",
                "JWT verification failed",
                "Too many failed attempts, account locked"
            }, 800, 1600);

        startWorker("PaymentService",
            new String[]{
                "Payment processed: $42.99",
                "Refund issued: $15.00",
                "Transaction authorized",
                "Card tokenized successfully",
                "Payment gateway response: OK"
            },
            new String[]{
                "Payment failed due to timeout",
                "Card declined: insufficient funds",
                "Gateway unreachable, retrying...",
                "Fraud check triggered — transaction held"
            }, 1000, 2000);

        startWorker("InventoryService",
            new String[]{
                "Stock updated: SKU-4821 +50 units",
                "Item reserved for order #8834",
                "Warehouse sync complete",
                "Low stock alert cleared: SKU-9921",
                "Restock confirmed from supplier"
            },
            new String[]{
                "Item out of stock: SKU-7723",
                "Inventory DB write timeout",
                "Sync failed — warehouse offline",
                "Negative stock detected for SKU-5500"
            }, 1200, 2400);

        startWorker("OrderService",
            new String[]{
                "Order #9102 placed successfully",
                "Order #8873 dispatched to courier",
                "Order #9003 delivered — feedback pending",
                "Cart converted: 3 items, $119.50",
                "Shipping label generated"
            },
            new String[]{
                "Order #9045 failed: payment issue",
                "Courier API timeout — retry queued",
                "Order stuck in processing for >10 min",
                "Duplicate order detected: #8801"
            }, 900, 1800);

        // Optional 5th service to show a failure scenario after ~15s
        // It stops sending after a short burst (simulated crash)
        Thread crashWorker = new Thread(() -> {
            try {
                // start normally
                WorkerNode node = new WorkerNode("NotificationService",
                    TCP_PORT,
                    new String[]{"Email sent: order confirmation", "SMS dispatched", "Push notification delivered"},
                    new String[]{"SMTP connection refused", "Notification queue overflow"},
                    600, 1200);

                Thread t = new Thread(node);
                t.start();
                Thread.sleep(14000); // run for 14 seconds then "crash"
                t.interrupt();
                System.out.println("[MainServer] NotificationService crashed (simulated).");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        crashWorker.setDaemon(true);
        crashWorker.start();

        System.out.println("[MainServer] System running. Dashboard → http://localhost:" + HTTP_PORT);

        // Keep main thread alive
        Thread.currentThread().join();
    }

    private static void startWorker(String name, String[] info, String[] errors,
                                     int minMs, int maxMs) {
        WorkerNode node = new WorkerNode(name, TCP_PORT, info, errors, minMs, maxMs);
        Thread t = new Thread(node, "worker-" + name);
        t.setDaemon(true);
        t.start();
    }
}