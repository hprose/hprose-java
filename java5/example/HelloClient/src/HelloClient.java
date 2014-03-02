
import hprose.client.HproseHttpClient;
import java.io.IOException;

public class HelloClient {
    public static void main(String[] args) throws IOException {
        HproseHttpClient client = new HproseHttpClient();
        client.useService("http://localhost:8080/HelloServer/Hello");
        String result = (String) client.invoke("sayHello", new Object[] { "Hprose" });
        System.out.println(result);
    }
}
