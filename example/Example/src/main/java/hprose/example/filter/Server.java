package hprose.example.filter;

import hprose.example.filter.compress.CompressFilter;
import hprose.example.filter.compress.SizeFilter;
import hprose.example.filter.stat.StatFilter;
import hprose.server.HproseTcpServer;
import java.io.IOException;
import java.net.URISyntaxException;

public class Server {
    public static Object echo(Object obj) {
        return obj;
    }
    public static void main(String[] args) throws URISyntaxException, IOException {
        HproseTcpServer server = new HproseTcpServer("tcp://0.0.0.0:8084");
        server.add("echo", Server.class);
        server.addFilter(new StatFilter());
        server.addFilter(new SizeFilter("Non compressed"));
        server.addFilter(new CompressFilter());
        server.addFilter(new SizeFilter("Compressed"));
        server.addFilter(new StatFilter());
        server.start();
        System.out.println("START");
        System.in.read();
        server.stop();
        System.out.println("STOP");
    }
}
