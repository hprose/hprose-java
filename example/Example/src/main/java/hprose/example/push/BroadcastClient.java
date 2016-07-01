package hprose.example.push;

import hprose.client.HproseClient;
import hprose.util.concurrent.Action;

interface IBroadcast {
    public void news(Action<String> callback);
    public String hello(String name);
}

public class BroadcastClient {
    public static void main(String[] args) throws Exception {
        final HproseClient client = HproseClient.create("tcp://127.0.0.1:8081");
        IBroadcast bc = client.useService(IBroadcast.class);
        bc.news((String news) -> System.out.println(news));
        Thread.sleep(100);
        System.out.println(bc.hello("Hprose"));
        Thread.sleep(10000);
    }
}
