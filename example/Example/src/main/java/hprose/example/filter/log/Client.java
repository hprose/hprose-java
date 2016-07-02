package hprose.example.filter.log;

import hprose.client.HproseClient;
import java.io.IOException;
import java.net.URISyntaxException;

interface IHello {
    String hello(String name);
}
public class Client {
    public static void main(String[] args) throws URISyntaxException, IOException {
        HproseClient client = HproseClient.create("tcp://127.0.0.1:8082");
        client.addFilter(new LogFilter());
        IHello h = client.useService(IHello.class);
        System.out.println(h.hello("World"));
    }
}
