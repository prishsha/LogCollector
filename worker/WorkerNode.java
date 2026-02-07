public class WorkerNode {

    public static void main(String[] args) throws Exception {

        if (args.length == 0) {
            System.out.println("Usage: java WorkerNode <service-name>");
            return;
        }

        String serviceName = args[0];

        while (true) {
            LogSender.sendLog("http://localhost:8080/log", serviceName);
            LogSender.sendHeartbeat("http://localhost:8080/heartbeat", serviceName);
            Thread.sleep(2000);
        }
    }
}
