package hprose.example.filterhandler.log;

import hprose.server.HproseTcpServer;
import java.io.IOException;
import java.net.URISyntaxException;

public class Server {
    public static String hello(String name) {
        return "Hello " + name + "!";
    }
    public static void main(String[] args) throws URISyntaxException, IOException {
        HproseTcpServer server = new HproseTcpServer("tcp://0.0.0.0:8086");
        server.add("hello", Server.class);
        server.beforeFilter.use(new LogHandler());
        server.start();
        System.out.println("START");
        System.in.read();
        server.stop();
        System.out.println("STOP");
    }
}
