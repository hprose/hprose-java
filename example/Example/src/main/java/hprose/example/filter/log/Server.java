package hprose.example.filter.log;

import hprose.server.HproseTcpServer;
import java.io.IOException;
import java.net.URISyntaxException;

public class Server {
    public static String hello(String name) {
        return "Hello " + name + "!";
    }
    public static void main(String[] args) throws URISyntaxException, IOException {
        HproseTcpServer server = new HproseTcpServer("tcp://0.0.0.0:8082");
        server.add("hello", Server.class);
        server.addFilter(new LogFilter());
        server.start();
        System.out.println("START");
        System.in.read();
        server.stop();
        System.out.println("STOP");
    }
}
