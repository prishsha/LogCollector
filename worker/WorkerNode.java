package worker;
public class WorkerNode {

    public static void main(String[] args) throws Exception {
        while (true) {
            LogSender.sendLog("http://localhost:8080/log");
            Thread.sleep(2000);
        }
    }
}
