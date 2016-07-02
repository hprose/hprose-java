package hprose.example.invokehandler.cache;

import hprose.client.HproseClient;
import hprose.common.InvokeSettings;
import hprose.common.MethodName;
import hprose.example.invokehandler.log.LogHandler;
import hprose.util.concurrent.Promise;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

interface IHello {
    String hello(String name);
    @MethodName("hello")
    Promise<String> asyncHello(String name, InvokeSettings settings);
    @MethodName("hello")
    Promise<String> asyncHello(Promise<String> name, InvokeSettings settings);
}

public class Client {
    public static void main(String[] args) throws URISyntaxException, IOException {
        HproseClient client = HproseClient.create("tcp://127.0.0.1:8085");
        client.use(new CacheHandler()).use(new LogHandler());
        IHello h = client.useService(IHello.class);
        Map<String, Object> userData = new HashMap<>();
        userData.put("cache", true);
        InvokeSettings settings = new InvokeSettings();
        settings.setUserData(userData);
        h.asyncHello("Cached Async World", settings)
         .then((String result) -> System.out.println(result));
        h.asyncHello(Promise.value("Cached Async World"), settings)
         .then((String result) -> System.out.println(result));
        System.out.println(h.hello("World"));
        System.out.println(h.hello("World"));
    }
}
