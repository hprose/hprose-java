package hprose.example.middleware.log;

import hprose.client.HproseClient;
import hprose.common.MethodName;
import hprose.util.concurrent.Promise;
import java.io.IOException;
import java.net.URISyntaxException;

interface IHello {
    String hello(String name);
    @MethodName("hello")
    Promise<String> asyncHello(String name);
    @MethodName("hello")
    Promise<String> asyncHello(Promise<String> name);
}

public class Client {
    public static void main(String[] args) throws URISyntaxException, IOException {
        HproseClient client = HproseClient.create("tcp://127.0.0.1:8085");
        client.use(new LogHandler());
        IHello h = client.useService(IHello.class);
        h.asyncHello("Async World")
         .then((String result) -> System.out.println(result));
        h.asyncHello(Promise.value("Async World"))
         .then((String result) -> System.out.println(result));
        System.out.println(h.hello("World"));
    }
}
