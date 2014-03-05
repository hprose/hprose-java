
import hprose.client.HproseTcpClient;
import hprose.common.HproseCallback1;
import java.io.IOException;
import java.net.URISyntaxException;

public class TCPHelloClient {
    public static void main(String[] args) throws IOException, URISyntaxException, InterruptedException {
        System.out.println("START");
        long start = System.currentTimeMillis();
        HproseTcpClient client = new HproseTcpClient("tcp://localhost:4321");
        final int[] n = new int[1];
        for (int i = 0; i < 10000; i++) {
            client.invoke("hello", new Object[] {"World"});
        }
        for (int i = 0; i < 10000; i++) {
            client.invoke("hello", new Object[] {"World"}, new HproseCallback1() {
                @Override
                public void handler(Object result) {
                }
            });
        }
        long end = System.currentTimeMillis();
        System.out.println(end - start);
        System.out.println("END");
    }
}
