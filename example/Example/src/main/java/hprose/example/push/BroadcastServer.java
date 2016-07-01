package hprose.example.push;

import hprose.server.HproseTcpServer;
import hprose.server.TcpContext;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;

public class BroadcastServer {
    public static String hello(String name) {
        TcpContext context = HproseTcpServer.getCurrentContext();
        System.out.println(Arrays.toString(context.clients.idlist("news")));
        context.clients.broadcast("news", "this is a pushed message:" + name);
        return "Hello " + name + "! -- " +
                context.getSocket().getRemoteSocketAddress().toString();
    }
    public static void main(String[] args) throws URISyntaxException, IOException {
        HproseTcpServer server = new HproseTcpServer("tcp://0.0.0.0:8081");
        server.add("hello", BroadcastServer.class);
        server.publish("news");
        server.start();
        System.out.println("START");
        System.in.read();
        server.stop();
        System.out.println("STOP");
    }
}
