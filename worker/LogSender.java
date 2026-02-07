import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class LogSender {

    public static void sendLog(String urlStr, String serviceName) throws Exception {

        URL url = new URL(urlStr);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();

        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setDoOutput(true);

        byte[] input = LogGenerator.generate(serviceName)
                .getBytes(StandardCharsets.UTF_8);

        con.getOutputStream().write(input);
        con.getInputStream();
    }
}
