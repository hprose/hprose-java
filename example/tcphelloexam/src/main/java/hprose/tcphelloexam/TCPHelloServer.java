package hprose.tcphelloexam;

import hprose.server.HproseTcpServer;
import java.io.IOException;
import java.net.URISyntaxException;

public class TCPHelloServer {
    public static String hello(String name) {
        return "Hello " + name + "!";
    }
    public static void main(String[] args) throws IOException, URISyntaxException, InterruptedException {
        HproseTcpServer server = new HproseTcpServer("tcp://localhost:4321");
        server.add("hello", TCPHelloServer.class);
        //server.setEnabledThreadPool(true);
        server.start();
        System.out.println("START");
        System.in.read();
        server.stop();
        System.out.println("STOP");
    }
}
