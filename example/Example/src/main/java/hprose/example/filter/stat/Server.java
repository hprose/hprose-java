package hprose.example.filter.stat;

import hprose.server.HproseTcpServer;
import java.io.IOException;
import java.net.URISyntaxException;

public class Server {
    public static Object echo(Object obj) {
        return obj;
    }
    public static void main(String[] args) throws URISyntaxException, IOException {
        HproseTcpServer server = new HproseTcpServer("tcp://0.0.0.0:8083");
        server.add("echo", Server.class);
        server.addFilter(new StatFilter());
        server.start();
        System.out.println("START");
        System.in.read();
        server.stop();
        System.out.println("STOP");
    }
}
