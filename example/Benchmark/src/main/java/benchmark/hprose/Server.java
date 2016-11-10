package benchmark.hprose;

import hprose.server.HproseTcpServer;
import java.io.IOException;
import java.net.URISyntaxException;

public class Server {
    public static void main(String[] args) throws IOException, URISyntaxException, InterruptedException {
        HproseTcpServer server = new HproseTcpServer("tcp://localhost:4321");
        server.setReactorThreads(Runtime.getRuntime().availableProcessors());
        server.add(new ServiceImpl());
        server.start();
        System.out.println("START");
        System.in.read();
        server.stop();
        System.out.println("STOP");
    }
}
