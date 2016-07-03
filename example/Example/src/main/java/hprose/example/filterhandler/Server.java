package hprose.example.filterhandler;

import hprose.example.filter.compress.CompressFilter;
import hprose.example.filterhandler.size.SizeHandler;
import hprose.example.filterhandler.stat.StatHandler;
import hprose.server.HproseTcpServer;
import java.io.IOException;
import java.net.URISyntaxException;

public class Server {
    public static Object echo(Object obj) {
        return obj;
    }
    public static void main(String[] args) throws URISyntaxException, IOException {
        HproseTcpServer server = new HproseTcpServer("tcp://0.0.0.0:8087");
        server.add("echo", Server.class);
        server.beforeFilter.use(new StatHandler("BeforeFilter"))
                           .use(new SizeHandler("Compresssed"));
        server.addFilter(new CompressFilter());
        server.afterFilter.use(new StatHandler("AfterFilter"))
                          .use(new SizeHandler("Non Compresssed"));
        server.start();
        System.out.println("START");
        System.in.read();
        server.stop();
        System.out.println("STOP");
    }
}
