package hprose.example.filterhandler.log;

import hprose.client.HproseClient;
import java.io.IOException;
import java.net.URISyntaxException;

interface IHello {
    String hello(String name);
}
public class Client {
    public static void main(String[] args) throws URISyntaxException, IOException {
        HproseClient client = HproseClient.create("tcp://127.0.0.1:8086");
        client.beforeFilter.use(new LogHandler());
        IHello h = client.useService(IHello.class);
        System.out.println(h.hello("World"));
    }
}
