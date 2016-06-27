package hprose.example.client;

import hprose.client.HproseClient;
import hprose.common.InvokeSettings;
import hprose.util.concurrent.Promise;

public class Exam3 {
    public static void main(String[] args) throws Throwable {
        HproseClient client = HproseClient.create("http://www.hprose.com/example/");
        InvokeSettings settings = new InvokeSettings();
        settings.setAsync(true);
        Promise<String> result = (Promise<String>)client.invoke("hello", new Object[] { "World" }, settings);
        result.then((String value) -> System.out.println(value));
        Thread.sleep(1000);
    }
}
