
import hprose.client.HproseTcpClient;
import hprose.server.HproseTcpServer;
import java.io.IOException;
import java.net.URISyntaxException;

public class TCPHelloExam {
    public static String hello(String name) {
        return "Hello " + name + "!";
    }
    public static void main(String[] args) throws IOException, URISyntaxException, InterruptedException {
        HproseTcpServer server = new HproseTcpServer("tcp://localhost:4321");
        server.add("hello", TCPHelloExam.class);
        server.start();
        Thread.sleep(100);
        System.out.println("START");
        long start = System.currentTimeMillis();
        HproseTcpClient client = new HproseTcpClient("tcp://localhost:4321");
        final int[] n = new int[1];
        for (int i = 0; i < 100000; i++) {
            client.invoke("hello", new Object[] {"World"});
        }
        long end = System.currentTimeMillis();
        System.out.println(end - start);
        client.close();
        server.stop();
        System.out.println("STOP");
    }
}
